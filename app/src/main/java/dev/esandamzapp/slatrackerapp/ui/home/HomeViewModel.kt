package dev.esandamzapp.slatrackerapp.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esandamzapp.slatrackerapp.data.network.RetrofitClient
import dev.esandamzapp.slatrackerapp.data.network.SlaRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

// --- Modelos de UI ---

data class RequestData(
    val id: String,
    val title: String,
    val requestType: String, // Campo clave para agrupar las métricas
    val daysInProcess: Int,
    val requestDate: String,
    val entryDate: String?,
    val status: RequestStatus,
    val technologyBlock: String,
    val priority: RequestPriority
)

enum class RequestStatus {
    CUMPLE, NO_CUMPLE, PENDIENTE, ESCALADO, RIESGO
}

enum class RequestPriority {
    CRITICA, ALTA, MEDIA, BAJA
}

// Filtros de UI
data class DashboardFilters(
    val startDate: LocalDate = LocalDate.of(2023, 1, 1),
    val endDate: LocalDate = LocalDate.now().plusYears(5),
    val slaType: List<String> = listOf("Todos"),
    val status: List<String> = listOf("Todos"),
    val technologyBlock: List<String> = listOf("Todos"),
    val priority: List<String> = listOf("Todos")
)

// Estructura para las tarjetas de métricas específicas
data class SlaMetric(
    val label: String,        // Ej: "Cumplimiento Nuevo Personal"
    val percentage: Double,   // Ej: 65.85
    val complyingCount: Int,  // Ej: 27
    val totalCount: Int,      // Ej: 41
    val averageDays: Int      // Ej: 35 días
)

// KPIs de UI (Sincronizado con HomeScreen)
data class DashboardKpis(
    val globalEfficacy: Double = 0.0, // Eficacia Global
    val totalRequests: Int = 0,       // Total Solicitudes
    val pendingRequests: Int = 0,     // Para la campanita
    val averageDays: Int = 0,         // Promedio general

    // Lista dinámica de métricas por tipo
    val typeMetrics: List<SlaMetric> = emptyList(),

    // Datos para gráficos
    val complianceByBlock: Map<String, Int> = emptyMap(),
    val statusDistribution: Map<String, Int> = emptyMap()
)

class HomeViewModel : ViewModel() {

    private val _requests = MutableStateFlow<List<RequestData>>(emptyList())
    val requests: StateFlow<List<RequestData>> = _requests.asStateFlow()

    private val _filters = MutableStateFlow(DashboardFilters())
    val filters: StateFlow<DashboardFilters> = _filters.asStateFlow()

    private val _kpis = MutableStateFlow(DashboardKpis())
    val kpis: StateFlow<DashboardKpis> = _kpis.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var _allBloques: List<String> = emptyList()

    init {
        loadDashboardData()
    }

    fun updateFilters(newFilters: DashboardFilters) {
        _filters.value = newFilters
        loadDashboardData()
    }

    fun toggleFilterOption(currentList: List<String>, option: String): List<String> {
        return if (option == "Todos") {
            listOf("Todos")
        } else {
            val newList = currentList.toMutableList()
            if (newList.contains("Todos")) newList.remove("Todos")

            if (newList.contains(option)) newList.remove(option)
            else newList.add(option)

            if (newList.isEmpty()) listOf("Todos") else newList
        }
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                fetchDataFromBackend()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error API (Usando Fallback): ${e.message}", e)
                val fallbackData = generateFallbackMockData()
                _requests.value = fallbackData
                recalculateKpis(fallbackData)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchDataFromBackend() {
        val currentFilters = _filters.value

        // Cargar filtros maestros si es necesario
        if (_allBloques.isEmpty()) {
            try {
                val filtersResponse = withContext(Dispatchers.IO) { RetrofitClient.api.getDashboardFilters() }
                _allBloques = filtersResponse.data.bloquesTech
            } catch (e: Exception) {
                Log.w("HomeViewModel", "No se pudieron cargar los filtros maestros")
            }
        }

        val startDateStr = currentFilters.startDate.toString()
        val endDateStr = currentFilters.endDate.toString()

        val bloqueTechParam = if (currentFilters.technologyBlock.contains("Todos")) null else currentFilters.technologyBlock.joinToString(",")
        val tipoSolicitudParam = if (currentFilters.slaType.contains("Todos")) null else currentFilters.slaType.joinToString(",")
        val prioridadParam = if (currentFilters.priority.contains("Todos")) null else currentFilters.priority.joinToString(",")

        val cumpleSlaParam: Boolean? = when {
            currentFilters.status.contains("CUMPLE") && !currentFilters.status.any { it == "NO CUMPLE" || it == "RIESGO" } -> true
            currentFilters.status.any { it == "NO CUMPLE" || it == "RIESGO" } && !currentFilters.status.contains("CUMPLE") -> false
            else -> null
        }

        val apiResponse = withContext(Dispatchers.IO) {
            RetrofitClient.api.getSlaData(
                startDate = startDateStr,
                endDate = endDateStr,
                bloqueTech = bloqueTechParam,
                tipoSolicitud = tipoSolicitudParam,
                prioridad = prioridadParam,
                cumpleSla = cumpleSlaParam
            )
        }

        val mappedData = apiResponse.data.map { dto -> mapDtoToUiModel(dto) }
        _requests.value = mappedData
        recalculateKpis(mappedData)
    }

    private fun mapDtoToUiModel(dto: SlaRequestDto): RequestData {
        val prioEnum = when(dto.prioridad.uppercase()) {
            "CRITICA", "CRÍTICA" -> RequestPriority.CRITICA
            "ALTA" -> RequestPriority.ALTA
            "MEDIA" -> RequestPriority.MEDIA
            "BAJA" -> RequestPriority.BAJA
            else -> RequestPriority.MEDIA
        }

        val statusEnum = if (dto.cumpleSla) {
            RequestStatus.CUMPLE
        } else {
            if (dto.diasTranscurridos > dto.diasUmbralSla) RequestStatus.NO_CUMPLE
            else if (dto.diasTranscurridos >= (dto.diasUmbralSla * 0.8)) RequestStatus.RIESGO
            else RequestStatus.PENDIENTE
        }

        val fechaSafe = dto.fechaSolicitud ?: ""
        val rawDate = if (fechaSafe.length >= 10) fechaSafe.take(10) else fechaSafe
        val rawEntryDate = if ((dto.fechaIngreso ?: "").length >= 10) dto.fechaIngreso!!.take(10) else dto.fechaIngreso

        return RequestData(
            id = dto.id.toString(),
            title = "${dto.tipoSolicitud} - ${dto.nombrePersonal ?: "Sin asignar"}",
            requestType = dto.tipoSolicitud,
            daysInProcess = dto.diasTranscurridos,
            requestDate = rawDate,
            entryDate = if (rawEntryDate.isNullOrEmpty()) null else rawEntryDate,
            status = statusEnum,
            technologyBlock = dto.bloqueTech,
            priority = prioEnum
        )
    }

    private fun recalculateKpis(data: List<RequestData>) {
        val total = data.size

        val globalComplying = data.count { it.status == RequestStatus.CUMPLE }
        val globalEfficacy = if (total > 0) (globalComplying.toDouble() / total) * 100 else 0.0
        val pending = data.count { it.status == RequestStatus.PENDIENTE }
        val averageDays = if (total > 0) data.map { it.daysInProcess }.average().toInt() else 0

        val metricsByType = data.groupBy { it.requestType }.map { (type, requests) ->
            val typeTotal = requests.size
            val typeComplying = requests.count { it.status == RequestStatus.CUMPLE }
            val percentage = if (typeTotal > 0) (typeComplying.toDouble() / typeTotal) * 100 else 0.0
            val typeAvgDays = if (typeTotal > 0) requests.map { it.daysInProcess }.average().toInt() else 0

            SlaMetric(
                label = "Cumplimiento $type",
                percentage = percentage,
                complyingCount = typeComplying,
                totalCount = typeTotal,
                averageDays = typeAvgDays
            )
        }.sortedByDescending { it.totalCount }

        val complianceByBlock = if (_filters.value.technologyBlock.contains("Todos") && _allBloques.isNotEmpty()) {
            _allBloques.associateWith { blockName ->
                val requestsForBlock = data.filter { it.technologyBlock == blockName }
                val ok = requestsForBlock.count { it.status == RequestStatus.CUMPLE }
                if (requestsForBlock.isNotEmpty()) (ok * 100) / requestsForBlock.size else 0
            }
        } else {
            data.groupBy { it.technologyBlock }.mapValues { (_, reqs) ->
                val ok = reqs.count { it.status == RequestStatus.CUMPLE }
                if (reqs.isNotEmpty()) (ok * 100) / reqs.size else 0
            }
        }

        val statusDist = data.groupBy { it.status.name }.mapValues { it.value.size }

        _kpis.value = DashboardKpis(
            globalEfficacy = globalEfficacy,
            totalRequests = total,
            pendingRequests = pending,
            averageDays = averageDays,
            typeMetrics = metricsByType,
            complianceByBlock = complianceByBlock,
            statusDistribution = statusDist
        )
    }

    private fun generateFallbackMockData(): List<RequestData> {
        return listOf(
            RequestData("M1", "Mock - Juan", "Nuevo Personal", 35, "2025-01-01", null, RequestStatus.CUMPLE, "Infraestructura", RequestPriority.ALTA),
            RequestData("M2", "Mock - Ana", "Reemplazo", 20, "2025-01-02", null, RequestStatus.NO_CUMPLE, "Desarrollo", RequestPriority.MEDIA)
        )
    }
}
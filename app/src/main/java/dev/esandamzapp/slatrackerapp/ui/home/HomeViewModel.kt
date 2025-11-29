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
    val requestType: String, // Clave para agrupación dinámica
    val daysInProcess: Int,
    val requestDate: String,
    val entryDate: String?,
    val status: RequestStatus,
    val technologyBlock: String, // Se mapea desde "area" o "bloqueTech" del DTO
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
    val endDate: LocalDate = LocalDate.now().plusYears(2),
    val slaType: List<String> = listOf("Todos"),        // Mapea a 'tipoSolicitud'
    val status: List<String> = listOf("Todos"),         // Mapea a 'cumpleSla'
    val technologyBlock: List<String> = listOf("Todos"),// Mapea a 'area'
    val priority: List<String> = listOf("Todos")        // Mapea a 'prioridad'
)

// Estructura para métricas dinámicas (Tarjetas superiores)
data class SlaMetric(
    val label: String,
    val percentage: Double,
    val complyingCount: Int,
    val totalCount: Int,
    val averageDays: Int
)

// Estado Global del Dashboard (Reactive Data)
data class DashboardKpis(
    val globalEfficacy: Double = 0.0,
    val totalRequests: Int = 0,
    val pendingRequests: Int = 0,
    val averageDays: Int = 0,

    val typeMetrics: List<SlaMetric> = emptyList(),

    // Datos para gráficos
    val complianceByBlock: Map<String, Int> = emptyMap(),
    val complianceByPriority: Map<String, Int> = emptyMap(),
    val statusDistribution: Map<String, Int> = emptyMap()
)

class HomeViewModel : ViewModel() {

    // Estado Reactivo
    private val _requests = MutableStateFlow<List<RequestData>>(emptyList())
    val requests: StateFlow<List<RequestData>> = _requests.asStateFlow()

    private val _filters = MutableStateFlow(DashboardFilters())
    val filters: StateFlow<DashboardFilters> = _filters.asStateFlow()

    private val _kpis = MutableStateFlow(DashboardKpis())
    val kpis: StateFlow<DashboardKpis> = _kpis.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Listas maestras para dropdowns (Cargadas desde Backend)
    private val _availableBlocks = MutableStateFlow(listOf("Todos"))
    val availableBlocks: StateFlow<List<String>> = _availableBlocks.asStateFlow()

    private val _availableTypes = MutableStateFlow(listOf("Todos"))
    val availableTypes: StateFlow<List<String>> = _availableTypes.asStateFlow()

    private val _availablePriorities = MutableStateFlow(listOf("Todos"))
    val availablePriorities: StateFlow<List<String>> = _availablePriorities.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Cargar filtros maestros (Catálogos)
                fetchFiltersFromBackend()
                // 2. Cargar datos del dashboard
                fetchDataFromBackend()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error en carga inicial: ${e.message}", e)
                generateFallbackData("Error de Red: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Paso: Usuario aplica filtros -> Actualizar datos
    fun updateFilters(newFilters: DashboardFilters) {
        _filters.value = newFilters
        viewModelScope.launch {
            _isLoading.value = true
            try {
                fetchDataFromBackend()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error actualizando filtros: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
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

    private suspend fun fetchFiltersFromBackend() {
        try {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.api.getDashboardFilters()
            }
            val data = response.data

            // Usamos elvis operator por si la lista viene nula y 'trim' para limpiar espacios
            val blocks = (data.bloquesTech ?: emptyList()).map { it.trim() }
            val types = (data.tiposSolicitud ?: emptyList()).map { it.trim() }
            val priorities = (data.prioridades ?: emptyList()).map { it.trim() }

            _availableBlocks.value = listOf("Todos") + blocks
            _availableTypes.value = listOf("Todos") + types
            _availablePriorities.value = listOf("Todos") + priorities

            Log.d("HomeViewModel", "Filtros cargados: ${blocks.size} áreas")
        } catch (e: Exception) {
            Log.w("HomeViewModel", "No se pudieron cargar los filtros: ${e.message}")
        }
    }

    private suspend fun fetchDataFromBackend() {
        val currentFilters = _filters.value

        // Preparación de Parámetros
        // Enviamos List<String> o null. Retrofit manejará la serialización a ?param=A&param=B

        val bloqueParam = if (currentFilters.technologyBlock.contains("Todos") || currentFilters.technologyBlock.isEmpty()) null
        else currentFilters.technologyBlock

        val tipoParam = if (currentFilters.slaType.contains("Todos") || currentFilters.slaType.isEmpty()) null
        else currentFilters.slaType

        val prioParam = if (currentFilters.priority.contains("Todos") || currentFilters.priority.isEmpty()) null
        else currentFilters.priority

        val cumpleParam: Boolean? = when {
            currentFilters.status.contains("CUMPLE") && !currentFilters.status.contains("NO_CUMPLE") -> true
            currentFilters.status.contains("NO_CUMPLE") && !currentFilters.status.contains("CUMPLE") -> false
            else -> null
        }

        // Formato UTC para evitar errores de PostgreSQL con DateTime
        val startDateStr = "${currentFilters.startDate}T00:00:00Z"
        val endDateStr = "${currentFilters.endDate}T23:59:59Z"

        Log.d("HomeViewModel", "Consultando datos... Bloques: $bloqueParam")

        // Llamada a la API
        val apiResponse = withContext(Dispatchers.IO) {
            RetrofitClient.api.getSlaData(
                startDate = startDateStr,
                endDate = endDateStr,
                bloqueTech = bloqueParam,   // Mapeado a "area" en SlaApiService
                tipoSolicitud = tipoParam,
                prioridad = prioParam,
                cumpleSla = cumpleParam
            )
        }

        // Procesamiento de datos (Mapeo DTO -> UI)
        val mappedData = apiResponse.data.mapNotNull { dto ->
            try {
                mapDtoToUiModel(dto)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error mapeando registro ID ${dto.id}: ${e.message}")
                null
            }
        }

        _requests.value = mappedData
        recalculateKpis(mappedData)
    }

    private fun mapDtoToUiModel(dto: SlaRequestDto): RequestData {
        val rawPrio = dto.prioridad ?: "Media"
        val prioEnum = try {
            when(rawPrio.trim().uppercase()) {
                "CRITICA", "CRÍTICA" -> RequestPriority.CRITICA
                "ALTA" -> RequestPriority.ALTA
                "MEDIA" -> RequestPriority.MEDIA
                "BAJA" -> RequestPriority.BAJA
                else -> RequestPriority.MEDIA
            }
        } catch (e: Exception) { RequestPriority.MEDIA }

        val statusEnum = if (dto.cumpleSla) {
            RequestStatus.CUMPLE
        } else {
            if (dto.diasTranscurridos > dto.diasUmbralSla) RequestStatus.NO_CUMPLE
            else if (dto.diasTranscurridos >= (dto.diasUmbralSla * 0.8)) RequestStatus.RIESGO
            else RequestStatus.PENDIENTE
        }

        val fechaSafe = dto.fechaSolicitud ?: LocalDate.now().toString()
        val rawDate = if (fechaSafe.length >= 10) fechaSafe.take(10) else fechaSafe

        val ingresoSafe = dto.fechaIngreso
        val rawEntryDate = if (ingresoSafe != null && ingresoSafe.length >= 10) ingresoSafe.take(10) else ingresoSafe

        return RequestData(
            id = dto.id.toString(),
            title = "${dto.tipoSolicitud ?: "Gral"} - ${dto.nombrePersonal ?: "N/A"}",
            requestType = dto.tipoSolicitud?.trim() ?: "General",
            daysInProcess = dto.diasTranscurridos,
            requestDate = rawDate,
            entryDate = rawEntryDate,
            status = statusEnum,
            technologyBlock = dto.bloqueTech?.trim() ?: "Sin Asignar",
            priority = prioEnum
        )
    }

    private fun recalculateKpis(data: List<RequestData>) {
        val total = data.size

        if (total == 0) {
            _kpis.value = DashboardKpis()
            return
        }

        val globalComplying = data.count { it.status == RequestStatus.CUMPLE }
        val globalEfficacy = if (total > 0) (globalComplying.toDouble() / total) * 100 else 0.0
        val pending = data.count { it.status == RequestStatus.PENDIENTE }
        val avgDays = if (total > 0) data.map { it.daysInProcess }.average().toInt() else 0

        // Métricas dinámicas por Tipo de Solicitud (Tarjetas superiores)
        val metricsByType = data.groupBy { it.requestType }.map { (type, requests) ->
            val subTotal = requests.size
            val subComplying = requests.count { it.status == RequestStatus.CUMPLE }
            val subPct = if (subTotal > 0) (subComplying.toDouble() / subTotal) * 100 else 0.0
            val subAvg = if (subTotal > 0) requests.map { it.daysInProcess }.average().toInt() else 0

            SlaMetric(
                label = "Cumplimiento $type",
                percentage = subPct,
                complyingCount = subComplying,
                totalCount = subTotal,
                averageDays = subAvg
            )
        }.sortedByDescending { it.totalCount }

        // Gráfico 1: Por Bloque/Área
        // Si no hay filtro específico, usamos la lista maestra para mostrar todas las áreas (incluso con 0%)
        // Si hay filtro, mostramos solo las áreas presentes en la data filtrada.
        val blocksSource = if (_availableBlocks.value.size > 1 && (_filters.value.technologyBlock.contains("Todos") || _filters.value.technologyBlock.isEmpty())) {
            _availableBlocks.value.filter { it != "Todos" }
        } else {
            data.map { it.technologyBlock }.distinct()
        }

        val complianceByBlock = blocksSource.associateWith { block ->
            val reqs = data.filter { it.technologyBlock.equals(block, ignoreCase = true) }
            if (reqs.isNotEmpty()) (reqs.count { it.status == RequestStatus.CUMPLE } * 100) / reqs.size else 0
        }

        // Gráfico 2: Por Prioridad
        val prioritiesSource = if (_availablePriorities.value.size > 1 && (_filters.value.priority.contains("Todos") || _filters.value.priority.isEmpty())) {
            _availablePriorities.value.filter { it != "Todos" }
        } else {
            data.map { it.priority.name }.distinct()
        }

        val complianceByPriority = prioritiesSource.associateWith { priorityName ->
            val reqs = data.filter { it.priority.name.equals(priorityName, ignoreCase = true) }
            if (reqs.isNotEmpty()) (reqs.count { it.status == RequestStatus.CUMPLE } * 100) / reqs.size else 0
        }

        val statusDist = data.groupBy { it.status.name }.mapValues { it.value.size }

        _kpis.value = DashboardKpis(
            globalEfficacy = globalEfficacy,
            totalRequests = total,
            pendingRequests = pending,
            averageDays = avgDays,
            typeMetrics = metricsByType,
            complianceByBlock = complianceByBlock,
            complianceByPriority = complianceByPriority,
            statusDistribution = statusDist
        )
    }

    private fun generateFallbackData(errorMessage: String) {
        val mock = listOf(
            RequestData("ERR", errorMessage, "ERROR RED", 0, "2025-01-01", null, RequestStatus.RIESGO, "Backend", RequestPriority.CRITICA)
        )
        _requests.value = mock
        recalculateKpis(mock)
    }
}
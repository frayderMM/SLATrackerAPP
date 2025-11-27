package dev.esandamzapp.slatrackerapp.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

// --- Data Models ---

data class RequestData(
    val id: String,
    val title: String,
    val slaType: String, // e.g., "SLA1", "SLA2"
    val daysInProcess: Int,
    val requestDate: LocalDate,
    val entryDate: LocalDate?,
    val status: RequestStatus,
    val technologyBlock: String,
    val priority: RequestPriority
)

// CORRECCIÓN: Se añade RIESGO al enum para evitar el error de referencia
enum class RequestStatus {
    CUMPLE, NO_CUMPLE, PENDIENTE, ESCALADO, RIESGO
}

enum class RequestPriority {
    CRITICA, ALTA, MEDIA, BAJA
}

// Filtros con soporte para selección múltiple (List<String>)
data class DashboardFilters(
    val startDate: LocalDate = LocalDate.now().minusMonths(1),
    val endDate: LocalDate = LocalDate.now(),
    val slaType: List<String> = listOf("Todos"),
    val status: List<String> = listOf("Todos"),
    val technologyBlock: List<String> = listOf("Todos"),
    val priority: List<String> = listOf("Todos")
)

data class DashboardKpis(
    val complianceRate: Int = 0,
    val averageDays: Int = 0,
    val totalRequests: Int = 0,
    val pendingRequests: Int = 0,
    val escalatedRequests: Int = 0,
    // Datos pre-calculados para los gráficos
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

    init {
        loadDashboardData()
    }

    // Actualiza el estado completo de los filtros
    fun updateFilters(newFilters: DashboardFilters) {
        _filters.value = newFilters
        applyFiltersAndRecalculateKpis()
    }

    /**
     * Lógica inteligente para chips de selección múltiple.
     * - Si se selecciona "Todos": limpia la lista y deja solo "Todos".
     * - Si se selecciona un item específico: quita "Todos" y añade/quita el item.
     * - Si la lista queda vacía: vuelve a poner "Todos".
     */
    fun toggleFilterOption(
        currentList: List<String>,
        option: String
    ): List<String> {
        return if (option == "Todos") {
            listOf("Todos")
        } else {
            val newList = currentList.toMutableList()
            if (newList.contains("Todos")) {
                newList.remove("Todos")
            }

            if (newList.contains(option)) {
                newList.remove(option)
            } else {
                newList.add(option)
            }

            if (newList.isEmpty()) listOf("Todos") else newList
        }
    }

    fun loadDashboardData() {
        val mockData = generateMockRequests()
        _requests.value = mockData
        applyFiltersAndRecalculateKpis()
    }

    private fun applyFiltersAndRecalculateKpis() {
        val currentFilters = _filters.value
        val allRequests = _requests.value

        val filteredRequests = allRequests.filter { request ->
            // 1. Filtro Fecha
            val dateFilter = !request.requestDate.isBefore(currentFilters.startDate) &&
                    !request.requestDate.isAfter(currentFilters.endDate)

            // 2. Filtros de Listas (Múltiples)
            val slaFilter = currentFilters.slaType.contains("Todos") ||
                    currentFilters.slaType.contains(request.slaType)

            // Normalizamos el nombre del status para comparar (ej: NO_CUMPLE -> "NO CUMPLE")
            val statusLabel = request.status.name // Usamos name directo para coincidir con la UI
            val statusFilter = currentFilters.status.contains("Todos") ||
                    currentFilters.status.contains(statusLabel)

            val techBlockFilter = currentFilters.technologyBlock.contains("Todos") ||
                    currentFilters.technologyBlock.contains(request.technologyBlock)

            val priorityFilter = currentFilters.priority.contains("Todos") ||
                    currentFilters.priority.contains(request.priority.name)

            dateFilter && slaFilter && statusFilter && techBlockFilter && priorityFilter
        }

        // --- Cálculo de KPIs ---
        val total = filteredRequests.size
        val complying = filteredRequests.count { it.status == RequestStatus.CUMPLE }
        val pending = filteredRequests.count { it.status == RequestStatus.PENDIENTE }
        val escalated = filteredRequests.count { it.status == RequestStatus.ESCALADO }

        val complianceRate = if (total > 0) (complying * 100) / total else 0
        val averageDays = if (total > 0) filteredRequests.map { it.daysInProcess }.average().toInt() else 0

        // --- Cálculo para Gráficos ---

        // 1. Cumplimiento por Bloque
        // Agrupar por bloque, calcular % de CUMPLE para cada uno
        val blocks = filteredRequests.groupBy { it.technologyBlock }
        val complianceByBlock = blocks.mapValues { (_, reqs) ->
            val ok = reqs.count { it.status == RequestStatus.CUMPLE }
            if (reqs.isNotEmpty()) (ok * 100) / reqs.size else 0
        }

        // 2. Distribución por Estado
        val statusDist = filteredRequests
            .groupBy { it.status.name }
            .mapValues { it.value.size }

        _kpis.value = DashboardKpis(
            complianceRate = complianceRate,
            averageDays = averageDays,
            totalRequests = total,
            pendingRequests = pending,
            escalatedRequests = escalated,
            complianceByBlock = complianceByBlock,
            statusDistribution = statusDist
        )
    }

    private fun generateMockRequests(): List<RequestData> {
        val today = LocalDate.now()
        // Generador simple de datos variados
        return listOf(
            RequestData("REQ01", "Dev Frontend", "SLA1", 25, today.minusDays(30), today.minusDays(5), RequestStatus.CUMPLE, "Frontend", RequestPriority.ALTA),
            RequestData("REQ02", "HR Manager", "SLA2", 46, today.minusDays(60), null, RequestStatus.NO_CUMPLE, "RRHH", RequestPriority.CRITICA),
            RequestData("REQ03", "Data Analyst", "SLA1", 23, today.minusDays(28), today.minusDays(5), RequestStatus.CUMPLE, "Data Science", RequestPriority.MEDIA),
            RequestData("REQ04", "UX Designer", "SLA1", 19, today.minusDays(25), today.minusDays(6), RequestStatus.CUMPLE, "Diseño", RequestPriority.ALTA),
            RequestData("REQ05", "Accountant", "SLA2", 66, today.minusDays(70), null, RequestStatus.NO_CUMPLE, "Finanzas", RequestPriority.BAJA),
            RequestData("REQ06", "DevOps Eng", "SLA1", 23, today.minusDays(24), today.minusDays(1), RequestStatus.CUMPLE, "DevOps", RequestPriority.CRITICA),
            RequestData("REQ07", "Admin Asst", "SLA2", 27, today.minusDays(35), today.minusDays(8), RequestStatus.PENDIENTE, "RRHH", RequestPriority.MEDIA),
            RequestData("REQ08", "Product Mgr", "SLA1", 33, today.minusDays(40), null, RequestStatus.ESCALADO, "Producto", RequestPriority.ALTA),
            RequestData("REQ09", "Backend Dev", "SLA1", 15, today.minusDays(15), null, RequestStatus.CUMPLE, "Backend", RequestPriority.ALTA),
            // Este RIESGO ahora funcionará correctamente porque está en el enum
            RequestData("REQ10", "SecOps", "SLA1", 40, today.minusDays(45), null, RequestStatus.RIESGO, "DevOps", RequestPriority.CRITICA)
        )
    }
}
package dev.esandamzapp.slatrackerapp.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esandamzapp.slatrackerapp.data.remote.dto.ConfigSla
import dev.esandamzapp.slatrackerapp.data.remote.dto.DashboardSlaDto
import dev.esandamzapp.slatrackerapp.data.repository.StatisticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estados de la UI
data class StatisticsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    
    // KPIs
    val cumplimiento: Int = 0,
    val totalSolicitudes: Int = 0,
    val tiempoPromedio: Int = 0,
    val enAlerta: Int = 0,
    val porcentajeIncumplidas: Int = 0,
    
    // Detalle por rol
    val detallePorRol: List<BloqueTechDetail> = emptyList(),
    
    // Incumplimientos
    val totalIncumplimientos: Int = 0,
    val retrasoPromedio: Int = 0,
    val retrasoMaximo: Int = 0,
    val incumplimientosPorBloque: List<IncumplimientoDetalle> = emptyList(),
    
    // Filtros
    val selectedSlaType: String = "SLA1",
    val startDate: String = "",
    val endDate: String = "",
    val selectedBloquesTech: List<String> = emptyList(),
    val reportName: String = "",
    
    // Configuración SLA
    val configSla: List<ConfigSla> = emptyList(),
    val diasUmbral: Int = 30,
    
    // Datos raw
    val dashboardData: List<DashboardSlaDto> = emptyList()
)

class StatisticsViewModel : ViewModel() {

    private val repository = StatisticsRepository()

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadConfigSla()
        loadDashboardData()
    }

    private fun loadConfigSla() {
        viewModelScope.launch {
            repository.getConfigSla().onSuccess { configs ->
                _uiState.value = _uiState.value.copy(
                    configSla = configs,
                    diasUmbral = configs.find { it.codigoSla == "SLA1" }?.diasUmbral ?: 30
                )
            }
        }
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val currentState = _uiState.value
            val slaCode = when (currentState.selectedSlaType) {
                "SLA1" -> "SLA1"
                "SLA2" -> "SLA2"
                else -> null
            }
            
            val bloqueTech = if (currentState.selectedBloquesTech.isEmpty() || 
                                 currentState.selectedBloquesTech.contains("Todos")) {
                null
            } else {
                currentState.selectedBloquesTech.joinToString(",")
            }
            
            repository.getDashboardData(
                slaCode = slaCode,
                startDate = currentState.startDate.takeIf { it.isNotEmpty() },
                endDate = currentState.endDate.takeIf { it.isNotEmpty() },
                bloqueTech = bloqueTech
            ).onSuccess { data ->
                _uiState.value = _uiState.value.copy(
                    dashboardData = data,
                    isLoading = false
                )
                calculateKPIs(data)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Error al cargar datos"
                )
            }
        }
    }

    private fun calculateKPIs(data: List<DashboardSlaDto>) {
        if (data.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                cumplimiento = 0,
                totalSolicitudes = 0,
                tiempoPromedio = 0,
                enAlerta = 0,
                porcentajeIncumplidas = 0,
                detallePorRol = emptyList(),
                totalIncumplimientos = 0,
                retrasoPromedio = 0,
                retrasoMaximo = 0,
                incumplimientosPorBloque = emptyList()
            )
            return
        }

        val currentState = _uiState.value
        val isSla1 = currentState.selectedSlaType == "SLA1"
        val isSla2 = currentState.selectedSlaType == "SLA2"
        val diasUmbral = currentState.diasUmbral

        // Total de solicitudes
        val total = data.size

        // Cumplimiento SLA
        val cumpleSla = data.count { solicitud ->
            when {
                isSla1 -> solicitud.cumpleSla1 == true
                isSla2 -> solicitud.cumpleSla2 == true
                else -> solicitud.cumpleSla1 == true || solicitud.cumpleSla2 == true
            }
        }
        val cumplimiento = if (total > 0) (cumpleSla * 100) / total else 0

        // Tiempo promedio
        val tiempoPromedio = if (total > 0) {
            data.sumOf { it.diasTranscurridos } / total
        } else 0

        // En alerta (70-79% del umbral)
        val enAlerta = data.count { solicitud ->
            val porcentaje = when {
                isSla1 -> solicitud.porcentajeCompletadoSla1 ?: 0.0
                isSla2 -> solicitud.porcentajeCompletadoSla2 ?: 0.0
                else -> solicitud.porcentajeCompletadoSla1 ?: 0.0
            }
            porcentaje in 70.0..79.9
        }

        // Incumplidas
        val incumplidas = data.count { solicitud ->
            when {
                isSla1 -> solicitud.cumpleSla1 == false
                isSla2 -> solicitud.cumpleSla2 == false
                else -> solicitud.cumpleSla1 == false && solicitud.cumpleSla2 == false
            }
        }
        val porcentajeIncumplidas = if (total > 0) (incumplidas * 100) / total else 0

        // Detalle por rol (Bloque Tech)
        val detallePorRol = data
            .groupBy { it.bloqueTech ?: "Sin Bloque" }
            .map { (bloque, solicitudes) ->
                val solicitudesCount = solicitudes.size
                val cumpleSlaCount = solicitudes.count { solicitud ->
                    when {
                        isSla1 -> solicitud.cumpleSla1 == true
                        isSla2 -> solicitud.cumpleSla2 == true
                        else -> solicitud.cumpleSla1 == true || solicitud.cumpleSla2 == true
                    }
                }
                val slaPercentage = if (solicitudesCount > 0) {
                    (cumpleSlaCount * 100) / solicitudesCount
                } else 0
                
                val tiempoPromedioBloque = if (solicitudesCount > 0) {
                    solicitudes.sumOf { it.diasTranscurridos } / solicitudesCount
                } else 0

                BloqueTechDetail(
                    bloqueTech = bloque,
                    solicitudes = solicitudesCount,
                    slaPercentage = slaPercentage,
                    tiempoPromedio = tiempoPromedioBloque,
                    cumple = slaPercentage >= 80
                )
            }
            .sortedByDescending { it.solicitudes }

        // Incumplimientos
        val solicitudesIncumplidas = data.filter { solicitud ->
            when {
                isSla1 -> solicitud.cumpleSla1 == false
                isSla2 -> solicitud.cumpleSla2 == false
                else -> solicitud.cumpleSla1 == false && solicitud.cumpleSla2 == false
            }
        }

        val totalIncumplimientos = solicitudesIncumplidas.size
        
        val retrasoPromedio = if (totalIncumplimientos > 0) {
            solicitudesIncumplidas.sumOf { 
                maxOf(0, it.diasTranscurridos - diasUmbral)
            } / totalIncumplimientos
        } else 0

        val retrasoMaximo = if (totalIncumplimientos > 0) {
            solicitudesIncumplidas.maxOfOrNull { 
                maxOf(0, it.diasTranscurridos - diasUmbral)
            } ?: 0
        } else 0

        // Incumplimientos por bloque
        val incumplimientosPorBloque = solicitudesIncumplidas
            .groupBy { it.bloqueTech ?: "Sin Bloque" }
            .map { (bloque, solicitudes) ->
                val incumplimientosCount = solicitudes.size
                val porcentajeDelTotal = if (totalIncumplimientos > 0) {
                    (incumplimientosCount * 100) / totalIncumplimientos
                } else 0
                
                val retrasoPromedioBloque = if (incumplimientosCount > 0) {
                    solicitudes.sumOf { 
                        maxOf(0, it.diasTranscurridos - diasUmbral)
                    } / incumplimientosCount
                } else 0

                IncumplimientoDetalle(
                    bloqueTech = bloque,
                    incumplimientos = incumplimientosCount,
                    porcentajeDelTotal = porcentajeDelTotal,
                    retrasoPromedio = retrasoPromedioBloque
                )
            }
            .sortedByDescending { it.incumplimientos }

        _uiState.value = _uiState.value.copy(
            cumplimiento = cumplimiento,
            totalSolicitudes = total,
            tiempoPromedio = tiempoPromedio,
            enAlerta = enAlerta,
            porcentajeIncumplidas = porcentajeIncumplidas,
            detallePorRol = detallePorRol,
            totalIncumplimientos = totalIncumplimientos,
            retrasoPromedio = retrasoPromedio,
            retrasoMaximo = retrasoMaximo,
            incumplimientosPorBloque = incumplimientosPorBloque
        )
    }

    fun updateSlaType(slaType: String) {
        val diasUmbral = _uiState.value.configSla.find { 
            it.codigoSla == slaType 
        }?.diasUmbral ?: 30
        
        _uiState.value = _uiState.value.copy(
            selectedSlaType = slaType,
            diasUmbral = diasUmbral
        )
    }

    fun updateStartDate(date: String) {
        _uiState.value = _uiState.value.copy(startDate = date)
    }

    fun updateEndDate(date: String) {
        _uiState.value = _uiState.value.copy(endDate = date)
    }

    fun updateBloquesTech(bloques: List<String>) {
        _uiState.value = _uiState.value.copy(selectedBloquesTech = bloques)
    }

    fun updateReportName(name: String) {
        _uiState.value = _uiState.value.copy(reportName = name)
    }

    fun clearFilters() {
        _uiState.value = _uiState.value.copy(
            selectedSlaType = "SLA1",
            startDate = "",
            endDate = "",
            selectedBloquesTech = emptyList(),
            diasUmbral = _uiState.value.configSla.find { it.codigoSla == "SLA1" }?.diasUmbral ?: 30
        )
    }

    fun applyFilters() {
        loadDashboardData()
    }
}

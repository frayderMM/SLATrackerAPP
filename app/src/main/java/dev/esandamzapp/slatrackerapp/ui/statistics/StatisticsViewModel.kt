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
import java.text.SimpleDateFormat
import java.util.*

// Estados de la UI
data class StatisticsUiState(
    val isLoading: Boolean = false,
    val isLoadingInitialData: Boolean = true,
    val error: String? = null,
    
    // KPIs (solo se actualizan al aplicar filtros)
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
    
    // Filtros (temporales, se aplican al presionar el botón)
    val selectedSlaType: String = "SLA1",
    val startDate: String = "",
    val endDate: String = "",
    val selectedBloquesTech: List<String> = emptyList(),
    val reportName: String = "Reporte Indicadores SLA",
    
    // Filtros aplicados (usados para calcular KPIs)
    val appliedSlaType: String = "SLA1",
    val appliedStartDate: String = "",
    val appliedEndDate: String = "",
    val appliedBloquesTech: List<String> = emptyList(),
    
    // Configuración SLA
    val configSla: List<ConfigSla> = emptyList(),
    val diasUmbral: Int = 30,
    val slaDescripcion: String = "",
    
    // Bloques Tech disponibles
    val bloquesTechDisponibles: List<String> = emptyList(),
    
    // Datos raw
    val dashboardData: List<DashboardSlaDto> = emptyList()
)

class StatisticsViewModel : ViewModel() {

    private val repository = StatisticsRepository()

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        initializeDefaultDates()
        loadInitialData()
    }

    private fun initializeDefaultDates() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        
        // Fecha fin: hoy
        val endDate = dateFormat.format(calendar.time)
        
        // Fecha inicio: 2 meses antes
        calendar.add(Calendar.MONTH, -2)
        val startDate = dateFormat.format(calendar.time)
        
        _uiState.value = _uiState.value.copy(
            startDate = startDate,
            endDate = endDate
        )
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingInitialData = true)
            
            // Cargar ConfigSla
            repository.getConfigSla().onSuccess { configs ->
                val sla1 = configs.find { it.codigoSla == "SLA1" }
                _uiState.value = _uiState.value.copy(
                    configSla = configs,
                    diasUmbral = sla1?.diasUmbral ?: 30,
                    slaDescripcion = sla1?.descripcion ?: "Contratación de nuevo personal"
                )
            }
            
            // Cargar Bloques Tech
            repository.getRolesRegistro().onSuccess { roles ->
                val bloquesTech = roles
                    .mapNotNull { it.bloqueTech }
                    .distinct()
                    .sorted()
                
                _uiState.value = _uiState.value.copy(
                    bloquesTechDisponibles = bloquesTech,
                    isLoadingInitialData = false
                )
                
                // Cargar datos iniciales automáticamente con filtros por defecto
                applyFilters()
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoadingInitialData = false)
            }
        }
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val currentState = _uiState.value
            // Usar filtros APLICADOS, no seleccionados
            val slaCode = when (currentState.appliedSlaType) {
                "SLA1" -> "SLA1"
                "SLA2" -> "SLA2"
                else -> null
            }
            
            // NO enviar bloqueTech al backend - filtrar en frontend como en la web
            // El backend solo acepta UN bloque, no múltiples
            val bloqueTech: String? = null
            
            // Convertir fechas a formato ISO con timezone UTC (agregar T00:00:00Z)
            val startDateIso = if (currentState.appliedStartDate.isNotEmpty()) {
                "${currentState.appliedStartDate}T00:00:00Z"
            } else null
            
            val endDateIso = if (currentState.appliedEndDate.isNotEmpty()) {
                "${currentState.appliedEndDate}T23:59:59Z"
            } else null
            
            repository.getDashboardData(
                slaCode = slaCode,
                startDate = startDateIso,
                endDate = endDateIso,
                bloqueTech = bloqueTech
            ).onSuccess { data ->
                // Filtrar por bloques tech en el FRONTEND (igual que en web)
                val filteredData = if (currentState.appliedBloquesTech.isEmpty()) {
                    // Si está vacío, mostrar todos
                    data
                } else {
                    // Filtrar solo los bloques seleccionados
                    data.filter { solicitud ->
                        currentState.appliedBloquesTech.contains(solicitud.bloqueTech)
                    }
                }
                
                _uiState.value = _uiState.value.copy(
                    dashboardData = filteredData,
                    isLoading = false
                )
                calculateKPIs(filteredData)
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
        // Usar SLA APLICADO para calcular KPIs
        val isSla1 = currentState.appliedSlaType == "SLA1"
        val isSla2 = currentState.appliedSlaType == "SLA2"
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
        val slaConfig = _uiState.value.configSla.find { it.codigoSla == slaType }
        
        _uiState.value = _uiState.value.copy(
            selectedSlaType = slaType
            // NO actualizar appliedSlaType aquí, solo al presionar "Aplicar Filtros"
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
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val endDate = dateFormat.format(calendar.time)
        calendar.add(Calendar.MONTH, -2)
        val startDate = dateFormat.format(calendar.time)
        
        _uiState.value = _uiState.value.copy(
            selectedSlaType = "SLA1",
            startDate = startDate,
            endDate = endDate,
            selectedBloquesTech = emptyList()
        )
        
        // Aplicar filtros para recargar datos
        applyFilters()
    }

    fun applyFilters() {
        // Copiar filtros temporales a aplicados
        val currentState = _uiState.value
        val slaConfig = currentState.configSla.find { it.codigoSla == currentState.selectedSlaType }
        
        _uiState.value = currentState.copy(
            appliedSlaType = currentState.selectedSlaType,
            appliedStartDate = currentState.startDate,
            appliedEndDate = currentState.endDate,
            appliedBloquesTech = currentState.selectedBloquesTech,
            diasUmbral = slaConfig?.diasUmbral ?: 30,
            slaDescripcion = slaConfig?.descripcion ?: "",
            reportName = "Reporte Indicadores ${currentState.selectedSlaType} - ${slaConfig?.descripcion ?: ""}"
        )
        
        // Ahora sí cargar datos
        loadDashboardData()
    }
    
    fun generatePdf(context: android.content.Context) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                val pdfDocument = android.graphics.pdf.PdfDocument()
                
                val pageWidth = 595
                val pageHeight = 842
                val margin = 40f
                val usableHeight = pageHeight - (2 * margin)
                
                // Colores corporativos
                val colorPrimary = android.graphics.Color.rgb(7, 28, 77) // HeaderBlue
                val colorAccent = android.graphics.Color.rgb(255, 122, 0) // AccentOrange
                val colorGreen = android.graphics.Color.rgb(76, 175, 80)
                val colorOrange = android.graphics.Color.rgb(255, 152, 0)
                val colorRed = android.graphics.Color.rgb(244, 67, 54)
                val colorGray = android.graphics.Color.rgb(100, 100, 100)
                val colorLightGray = android.graphics.Color.rgb(246, 242, 255) // LightLilac
                val colorBorderGray = android.graphics.Color.rgb(227, 223, 245)
                
                var pageNum = 1
                var pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
                var page = pdfDocument.startPage(pageInfo)
                var canvas = page.canvas
                
                val paint = android.graphics.Paint()
                paint.isAntiAlias = true
                
                var yPos = margin
                
                // ============ ENCABEZADO ============
                paint.textSize = 16f
                paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                paint.color = colorPrimary
                canvas.drawText(currentState.reportName, margin, yPos, paint)
                yPos += 22f
                
                // Período y fecha
                paint.textSize = 9f
                paint.typeface = android.graphics.Typeface.DEFAULT
                paint.color = colorGray
                canvas.drawText("Período: ${currentState.appliedStartDate} al ${currentState.appliedEndDate}", margin, yPos, paint)
                yPos += 12f
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                canvas.drawText("Generado: ${dateFormat.format(Date())}", margin, yPos, paint)
                yPos += 20f
                
                // ============ SECCIÓN KPIs ============
                paint.textSize = 12f
                paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                paint.color = android.graphics.Color.BLACK
                canvas.drawText("Indicadores Clave (KPIs)", margin, yPos, paint)
                yPos += 15f
                
                // KPI Cards en grid 3x2 (estilo card con bordes redondeados)
                val kpiCardWidth = 165f
                val kpiCardHeight = 55f
                val kpiSpacing = 10f
                var kpiX = margin
                var kpiY = yPos
                
                val kpis = listOf(
                    Triple("Cumplimiento SLA", "${currentState.cumplimiento}%", currentState.cumplimiento),
                    Triple("Total Solicitudes", "${currentState.totalSolicitudes}", 100),
                    Triple("Tiempo Promedio", "${currentState.tiempoPromedio} días", 100),
                    Triple("En Alerta", "${currentState.enAlerta}", currentState.enAlerta),
                    Triple("% Incumplidas", "${currentState.porcentajeIncumplidas}%", currentState.porcentajeIncumplidas),
                    Triple("Días Umbral", "${currentState.diasUmbral}", 100)
                )
                
                kpis.forEachIndexed { index, (label, value, numVal) ->
                    // Fondo del card con color lilac
                    paint.color = colorLightGray
                    paint.style = android.graphics.Paint.Style.FILL
                    val rectF = android.graphics.RectF(kpiX, kpiY, kpiX + kpiCardWidth, kpiY + kpiCardHeight)
                    canvas.drawRoundRect(rectF, 6f, 6f, paint)
                    
                    // Borde del card
                    paint.color = colorBorderGray
                    paint.style = android.graphics.Paint.Style.STROKE
                    paint.strokeWidth = 1.5f
                    canvas.drawRoundRect(rectF, 6f, 6f, paint)
                    paint.style = android.graphics.Paint.Style.FILL
                    
                    // Label del KPI
                    paint.textSize = 8f
                    paint.color = colorGray
                    paint.typeface = android.graphics.Typeface.DEFAULT
                    canvas.drawText(label, kpiX + 10f, kpiY + 16f, paint)
                    
                    // Valor del KPI con color dinámico
                    paint.textSize = 22f
                    paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                    
                    // Color según el tipo y valor
                    when {
                        label.contains("Cumplimiento") -> {
                            paint.color = when {
                                numVal >= 80 -> colorGreen
                                numVal >= 70 -> colorOrange
                                else -> colorRed
                            }
                        }
                        label.contains("Incumpl") -> {
                            paint.color = if (numVal == 0) colorGreen else colorRed
                        }
                        label.contains("Alerta") -> {
                            paint.color = if (numVal == 0) colorGreen else colorOrange
                        }
                        else -> paint.color = colorPrimary
                    }
                    
                    canvas.drawText(value, kpiX + 10f, kpiY + 42f, paint)
                    
                    // Mover posición para siguiente card
                    if ((index + 1) % 3 == 0) {
                        kpiX = margin
                        kpiY += kpiCardHeight + kpiSpacing
                    } else {
                        kpiX += kpiCardWidth + kpiSpacing
                    }
                }
                
                yPos = kpiY + 15f
                
                // ============ TABLA: Detalle por Rol ============
                paint.textSize = 12f
                paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                paint.color = android.graphics.Color.BLACK
                canvas.drawText("Detalle de Cumplimiento por Rol", margin, yPos, paint)
                yPos += 15f
                
                // Verificar si cabe la tabla en esta página
                val rowHeight = 22f
                val headerHeight = 28f
                val numRows = minOf(currentState.detallePorRol.size, 15)
                val tableHeight = headerHeight + (numRows * rowHeight)
                
                if (yPos + tableHeight > pageHeight - margin) {
                    // No cabe, nueva página
                    pdfDocument.finishPage(page)
                    pageNum++
                    pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    yPos = margin
                    
                    paint.textSize = 12f
                    paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                    paint.color = android.graphics.Color.BLACK
                    canvas.drawText("Detalle de Cumplimiento por Rol", margin, yPos, paint)
                    yPos += 15f
                }
                
                // Encabezado de tabla con color azul-gris
                paint.color = android.graphics.Color.rgb(96, 125, 139)
                paint.style = android.graphics.Paint.Style.FILL
                canvas.drawRect(margin, yPos, pageWidth - margin, yPos + headerHeight, paint)
                
                // Textos de encabezado
                paint.color = android.graphics.Color.WHITE
                paint.textSize = 9f
                paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                
                val colWidths = listOf(110f, 85f, 95f, 80f, 85f)
                var colX = margin + 8f
                canvas.drawText("BLOQUE TECH", colX, yPos + 18f, paint)
                colX += colWidths[0]
                canvas.drawText("SOLICITUDES", colX, yPos + 18f, paint)
                colX += colWidths[1]
                canvas.drawText("TIEMPO PROM.", colX, yPos + 18f, paint)
                colX += colWidths[2]
                canvas.drawText("SLA (%)", colX, yPos + 18f, paint)
                colX += colWidths[3]
                canvas.drawText("INDICADOR", colX, yPos + 18f, paint)
                
                yPos += headerHeight
                
                // Filas de datos
                paint.typeface = android.graphics.Typeface.DEFAULT
                paint.textSize = 8.5f
                currentState.detallePorRol.take(15).forEachIndexed { index, detalle ->
                    // Fondo alternado
                    val rowColor = if (index % 2 == 0) android.graphics.Color.WHITE else android.graphics.Color.rgb(248, 248, 248)
                    paint.color = rowColor
                    paint.style = android.graphics.Paint.Style.FILL
                    canvas.drawRect(margin, yPos, pageWidth - margin, yPos + rowHeight, paint)
                    
                    // Textos de la fila
                    paint.color = android.graphics.Color.BLACK
                    colX = margin + 8f
                    canvas.drawText(detalle.bloqueTech, colX, yPos + 15f, paint)
                    colX += colWidths[0]
                    canvas.drawText("${detalle.solicitudes}", colX, yPos + 15f, paint)
                    colX += colWidths[1]
                    canvas.drawText("${detalle.tiempoPromedio} días", colX, yPos + 15f, paint)
                    colX += colWidths[2]
                    canvas.drawText("${detalle.slaPercentage}%", colX, yPos + 15f, paint)
                    colX += colWidths[3]
                    
                    // Indicador con color
                    val indicatorColor = when {
                        detalle.slaPercentage >= 80 -> colorGreen
                        detalle.slaPercentage >= 70 -> colorOrange
                        else -> colorRed
                    }
                    val indicatorText = when {
                        detalle.slaPercentage >= 80 -> "Cumple"
                        detalle.slaPercentage >= 70 -> "Alerta"
                        else -> "Incumple"
                    }
                    paint.color = indicatorColor
                    paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                    canvas.drawText(indicatorText, colX, yPos + 15f, paint)
                    paint.typeface = android.graphics.Typeface.DEFAULT
                    
                    yPos += rowHeight
                }
                
                // Borde de tabla
                paint.color = android.graphics.Color.rgb(200, 200, 200)
                paint.style = android.graphics.Paint.Style.STROKE
                paint.strokeWidth = 1f
                canvas.drawRect(margin, yPos - (numRows * rowHeight) - headerHeight, pageWidth - margin, yPos, paint)
                paint.style = android.graphics.Paint.Style.FILL
                
                yPos += 20f
                
                // ============ ANÁLISIS DE INCUMPLIMIENTOS ============
                if (currentState.totalIncumplimientos > 0) {
                    val incHeaderHeight = 60f
                    val incTableHeight = headerHeight + (currentState.incumplimientosPorBloque.size * rowHeight)
                    val incTotalHeight = incHeaderHeight + incTableHeight + 20f
                    
                    // Verificar si cabe en esta página
                    if (yPos + incTotalHeight > pageHeight - margin) {
                        // Nueva página para incumplimientos
                        pdfDocument.finishPage(page)
                        pageNum++
                        pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
                        page = pdfDocument.startPage(pageInfo)
                        canvas = page.canvas
                        yPos = margin
                    }
                    
                    // Título de sección
                    paint.textSize = 13f
                    paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                    paint.color = colorRed
                    canvas.drawText("Análisis de Incumplimientos", margin, yPos, paint)
                    yPos += 20f
                    
                    // Resumen de incumplimientos
                    paint.textSize = 9f
                    paint.typeface = android.graphics.Typeface.DEFAULT
                    paint.color = android.graphics.Color.BLACK
                    canvas.drawText("Total: ${currentState.totalIncumplimientos}", margin, yPos, paint)
                    yPos += 14f
                    canvas.drawText("Retraso Promedio: ${currentState.retrasoPromedio} días", margin, yPos, paint)
                    yPos += 14f
                    canvas.drawText("Retraso Máximo: ${currentState.retrasoMaximo} días", margin, yPos, paint)
                    yPos += 20f
                    
                    // Subtítulo
                    paint.textSize = 11f
                    paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                    canvas.drawText("Incumplimientos por Bloque Tech", margin, yPos, paint)
                    yPos += 15f
                    
                    // Encabezado tabla incumplimientos
                    paint.color = android.graphics.Color.rgb(96, 125, 139)
                    paint.style = android.graphics.Paint.Style.FILL
                    canvas.drawRect(margin, yPos, pageWidth - margin, yPos + headerHeight, paint)
                    
                    paint.color = android.graphics.Color.WHITE
                    paint.textSize = 9f
                    paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                    
                    val col2Widths = listOf(140f, 120f, 90f, 105f)
                    colX = margin + 8f
                    canvas.drawText("BLOQUE TECH", colX, yPos + 18f, paint)
                    colX += col2Widths[0]
                    canvas.drawText("INCUMPLIMIENTOS", colX, yPos + 18f, paint)
                    colX += col2Widths[1]
                    canvas.drawText("% TOTAL", colX, yPos + 18f, paint)
                    colX += col2Widths[2]
                    canvas.drawText("RETRASO PROM.", colX, yPos + 18f, paint)
                    
                    yPos += headerHeight
                    
                    // Filas de incumplimientos
                    paint.typeface = android.graphics.Typeface.DEFAULT
                    paint.textSize = 8.5f
                    currentState.incumplimientosPorBloque.forEachIndexed { index, inc ->
                        val rowColor = if (index % 2 == 0) android.graphics.Color.WHITE else android.graphics.Color.rgb(248, 248, 248)
                        paint.color = rowColor
                        paint.style = android.graphics.Paint.Style.FILL
                        canvas.drawRect(margin, yPos, pageWidth - margin, yPos + rowHeight, paint)
                        
                        paint.color = android.graphics.Color.BLACK
                        colX = margin + 8f
                        canvas.drawText(inc.bloqueTech, colX, yPos + 15f, paint)
                        colX += col2Widths[0]
                        canvas.drawText("${inc.incumplimientos}", colX, yPos + 15f, paint)
                        colX += col2Widths[1]
                        canvas.drawText("${inc.porcentajeDelTotal}%", colX, yPos + 15f, paint)
                        colX += col2Widths[2]
                        canvas.drawText("${inc.retrasoPromedio} días", colX, yPos + 15f, paint)
                        
                        yPos += rowHeight
                    }
                    
                    // Borde tabla incumplimientos
                    paint.color = android.graphics.Color.rgb(200, 200, 200)
                    paint.style = android.graphics.Paint.Style.STROKE
                    paint.strokeWidth = 1f
                    canvas.drawRect(margin, yPos - (currentState.incumplimientosPorBloque.size * rowHeight) - headerHeight, 
                                   pageWidth - margin, yPos, paint)
                }
                
                pdfDocument.finishPage(page)
                
                // Guardar PDF
                val fileName = "Reporte_Indicadores_${currentState.appliedSlaType}_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
                val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
                val file = java.io.File(downloadsDir, fileName)
                
                pdfDocument.writeTo(java.io.FileOutputStream(file))
                pdfDocument.close()
                
                // Notificar al usuario
                android.widget.Toast.makeText(context, "PDF generado exitosamente", android.widget.Toast.LENGTH_LONG).show()
                
            } catch (e: Exception) {
                android.widget.Toast.makeText(context, "Error al generar PDF: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }
}

package dev.esandamzapp.slatrackerapp.ui.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.esandamzapp.slatrackerapp.ui.theme.SLATrackerAPPTheme
import java.text.SimpleDateFormat
import java.util.*

// Colores del proyecto
private val HeaderBlue = Color(0xFF071C4D)
private val AccentOrange = Color(0xFFFF7A00)
private val LightLilac = Color(0xFFF6F2FF)
private val CardBorderLilac = Color(0xFFE3DFF5)
private val KpiBackground = Color(0xFFF6F2FF)
private val ChipUnselected = Color(0xFFE0E0E0)
private val TextSoft = Color(0xFF666666)
private val AlertBackground = Color(0xFFFFF3E0)
private val AlertBorder = Color(0xFFFFB74D)

// Data classes para mockear datos
data class BloqueTechDetail(
    val bloqueTech: String,
    val solicitudes: Int,
    val slaPercentage: Int,
    val tiempoPromedio: Int,
    val cumple: Boolean
)

data class IncumplimientoDetalle(
    val bloqueTech: String,
    val incumplimientos: Int,
    val porcentajeDelTotal: Int,
    val retrasoPromedio: Int
)

@Composable
fun StatisticsScreen(
    onHistoryClick: () -> Unit = {},
    onGeneratePdfClick: () -> Unit = {},
    viewModel: StatisticsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var datePickerMode by remember { mutableStateOf("start") } // "start" o "end"

    val bloqueTechOptions = listOf("Todos", "Backend", "Frontend", "QA", "DevOps", "Infraestructura")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8FF))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            StatisticsHeader(onHistoryClick = onHistoryClick)

            // Loading o Error
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AccentOrange)
                    }
                }
                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Error: ${uiState.error}",
                                color = Color.Red,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadDashboardData() }) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
                else -> {
                    // Contenido scrolleable
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. FILTROS DEL REPORTE
                        item {
                            FiltrosReporteCard(
                                selectedSlaType = uiState.selectedSlaType,
                                onSlaTypeChange = { viewModel.updateSlaType(it) },
                                startDate = uiState.startDate,
                                onStartDateClick = {
                                    datePickerMode = "start"
                                    showDatePickerDialog = true
                                },
                                endDate = uiState.endDate,
                                onEndDateClick = {
                                    datePickerMode = "end"
                                    showDatePickerDialog = true
                                },
                                selectedBloquesTech = uiState.selectedBloquesTech,
                                bloqueTechOptions = bloqueTechOptions,
                                onBloquesTechChange = { viewModel.updateBloquesTech(it) },
                                onLimpiarFiltros = { viewModel.clearFilters() },
                                onAplicarFiltros = { viewModel.applyFilters() }
                            )
                        }

                        // 2. KPIs
                        item {
                            Text(
                                text = "KPIs - ${uiState.selectedSlaType}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = HeaderBlue,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        item {
                            KPIsGrid(
                                cumplimiento = uiState.cumplimiento,
                                totalSolicitudes = uiState.totalSolicitudes,
                                tiempoPromedio = uiState.tiempoPromedio,
                                enAlerta = uiState.enAlerta,
                                porcentajeIncumplidas = uiState.porcentajeIncumplidas,
                                startDate = uiState.startDate.ifEmpty { "N/A" },
                                endDate = uiState.endDate.ifEmpty { "N/A" }
                            )
                        }

                        // 3. DETALLE POR ROL
                        item {
                            DetallePorRolCard(
                                selectedSlaType = uiState.selectedSlaType,
                                detalles = uiState.detallePorRol
                            )
                        }

                        // 4. ANÁLISIS DE INCUMPLIMIENTOS
                        if (uiState.totalIncumplimientos > 0) {
                            item {
                                AnalisisIncumplimientosCard(
                                    selectedSlaType = uiState.selectedSlaType,
                                    totalIncumplimientos = uiState.totalIncumplimientos,
                                    retrasoPromedio = uiState.retrasoPromedio,
                                    retrasoMaximo = uiState.retrasoMaximo,
                                    totalSolicitudes = uiState.totalSolicitudes,
                                    diasUmbral = uiState.diasUmbral,
                                    incumplimientosPorBloque = uiState.incumplimientosPorBloque
                                )
                            }
                        }

                        // 5. CONFIGURACIÓN DEL REPORTE
                        item {
                            ConfiguracionReporteCard(
                                reportName = uiState.reportName,
                                onReportNameChange = { viewModel.updateReportName(it) },
                                sugerencia = "Reporte_SLA_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}"
                            )
                        }

                        // 6. BOTONES DE ACCIÓN
                        item {
                            BotonesAccionCard(
                                onGenerarPdf = onGeneratePdfClick,
                                onHistorial = onHistoryClick,
                                enabled = uiState.totalSolicitudes > 0
                            )
                        }

                        // Espaciado final
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }

    // DatePicker Dialog (mock - se implementará después)
    if (showDatePickerDialog) {
        AlertDialog(
            onDismissRequest = { showDatePickerDialog = false },
            title = { Text(if (datePickerMode == "start") "Seleccionar Fecha Inicio" else "Seleccionar Fecha Fin") },
            text = { Text("Formato: yyyy-MM-dd") },
            confirmButton = {
                TextButton(onClick = {
                    // Mock: asignar fecha actual
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val currentDate = dateFormat.format(Date())
                    if (datePickerMode == "start") {
                        viewModel.updateStartDate(currentDate)
                    } else {
                        viewModel.updateEndDate(currentDate)
                    }
                    showDatePickerDialog = false
                }) {
                    Text("Usar Fecha Actual")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// ============================================
// COMPONENTES
// ============================================

@Composable
private fun StatisticsHeader(onHistoryClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(HeaderBlue)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Reportes",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Genera y exporta reportes de cumplimiento SLA",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp
            )
        }

        IconButton(
            onClick = onHistoryClick,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = "Historial",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun FiltrosReporteCard(
    selectedSlaType: String,
    onSlaTypeChange: (String) -> Unit,
    startDate: String,
    onStartDateClick: () -> Unit,
    endDate: String,
    onEndDateClick: () -> Unit,
    selectedBloquesTech: List<String>,
    bloqueTechOptions: List<String>,
    onBloquesTechChange: (List<String>) -> Unit,
    onLimpiarFiltros: () -> Unit,
    onAplicarFiltros: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FilterAlt,
                    contentDescription = null,
                    tint = HeaderBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Filtros del Reporte",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = HeaderBlue
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tipo de SLA (obligatorio)
            Text(
                text = "Tipo de SLA *",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(listOf("SLA1", "SLA2", "Todos")) { slaType ->
                    FilterChip(
                        selected = selectedSlaType == slaType,
                        onClick = { onSlaTypeChange(slaType) },
                        label = { Text(slaType) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentOrange,
                            selectedLabelColor = Color.White,
                            containerColor = ChipUnselected
                        )
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // Fechas
            Text(
                text = "Período",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = startDate.ifEmpty { "" },
                    onValueChange = {},
                    label = { Text("Fecha Inicio", fontSize = 12.sp) },
                    readOnly = true,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onStartDateClick() },
                    leadingIcon = {
                        Icon(Icons.Default.Event, contentDescription = null, tint = HeaderBlue)
                    },
                    placeholder = { Text("Seleccionar", fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HeaderBlue,
                        unfocusedBorderColor = CardBorderLilac
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = endDate.ifEmpty { "" },
                    onValueChange = {},
                    label = { Text("Fecha Fin", fontSize = 12.sp) },
                    readOnly = true,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onEndDateClick() },
                    leadingIcon = {
                        Icon(Icons.Default.Event, contentDescription = null, tint = HeaderBlue)
                    },
                    placeholder = { Text("Seleccionar", fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HeaderBlue,
                        unfocusedBorderColor = CardBorderLilac
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bloque Tech (multi-select)
            Text(
                text = "Bloque Tech (Opcional)",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(bloqueTechOptions) { bloque ->
                    val isSelected = selectedBloquesTech.contains(bloque)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            onBloquesTechChange(
                                if (bloque == "Todos") {
                                    if (isSelected) emptyList() else listOf("Todos")
                                } else {
                                    if (isSelected) {
                                        selectedBloquesTech - bloque
                                    } else {
                                        (selectedBloquesTech - "Todos") + bloque
                                    }
                                }
                            )
                        },
                        label = { Text(bloque, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = HeaderBlue,
                            selectedLabelColor = Color.White,
                            containerColor = ChipUnselected
                        )
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onLimpiarFiltros) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Limpiar", fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onAplicarFiltros,
                    colors = ButtonDefaults.buttonColors(containerColor = HeaderBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterAlt,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Aplicar Filtros", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun KPIsGrid(
    cumplimiento: Int,
    totalSolicitudes: Int,
    tiempoPromedio: Int,
    enAlerta: Int,
    porcentajeIncumplidas: Int,
    startDate: String,
    endDate: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Fila 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KpiCard(
                title = "Cumplimiento",
                value = "$cumplimiento%",
                subtitle = "",
                modifier = Modifier.weight(1f),
                valueColor = when {
                    cumplimiento >= 80 -> Color(0xFF4CAF50)
                    cumplimiento >= 70 -> Color(0xFFFFA726)
                    else -> Color(0xFFEF5350)
                }
            )
            KpiCard(
                title = "Total Solicitudes",
                value = "$totalSolicitudes",
                subtitle = "Procesadas",
                modifier = Modifier.weight(1f),
                valueColor = HeaderBlue
            )
        }

        // Fila 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KpiCard(
                title = "Tiempo Promedio",
                value = "$tiempoPromedio",
                subtitle = "días (de 30)",
                modifier = Modifier.weight(1f),
                valueColor = Color(0xFF29B6F6)
            )
            KpiCard(
                title = "En Alerta",
                value = "$enAlerta",
                subtitle = "Riesgo (70-79%)",
                modifier = Modifier.weight(1f),
                valueColor = Color(0xFFFFA726)
            )
        }

        // Fila 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KpiCard(
                title = "% Incumplidas",
                value = "$porcentajeIncumplidas%",
                subtitle = "${(totalSolicitudes * porcentajeIncumplidas / 100)} solicitudes",
                modifier = Modifier.weight(1f),
                valueColor = Color(0xFFEF5350)
            )
            KpiCard(
                title = "Período",
                value = startDate,
                subtitle = "↓\n$endDate",
                modifier = Modifier.weight(1f),
                valueColor = HeaderBlue,
                valueSize = 14.sp
            )
        }
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    valueColor: Color = HeaderBlue,
    valueSize: androidx.compose.ui.unit.TextUnit = 24.sp
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = KpiBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLilac)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSoft,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = valueSize,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                textAlign = TextAlign.Center
            )
            if (subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = TextSoft,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun DetallePorRolCard(
    selectedSlaType: String,
    detalles: List<BloqueTechDetail>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.TableChart,
                    contentDescription = null,
                    tint = HeaderBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Detalle por Rol - $selectedSlaType",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = HeaderBlue
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Encabezados
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightLilac, RoundedCornerShape(8.dp))
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("BLOQUE TECH", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
                Text("SOL.", fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.weight(0.7f))
                Text("SLA", fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.weight(0.7f))
                Text("T. PROM.", fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.weight(0.9f))
                Text("IND.", fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.weight(0.5f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Filas de datos
            detalles.forEach { detalle ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(detalle.bloqueTech, fontSize = 12.sp, modifier = Modifier.weight(1.2f))
                    Text("${detalle.solicitudes}", fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(0.7f))
                    Text(
                        text = "${detalle.slaPercentage}%",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(0.7f)
                            .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                            .padding(vertical = 4.dp)
                    )
                    Text(
                        text = "${detalle.tiempoPromedio} días",
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        modifier = Modifier
                            .weight(0.9f)
                            .background(Color(0xFF29B6F6), RoundedCornerShape(8.dp))
                            .padding(vertical = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                if (detalle.cumple) Color(0xFF4CAF50) else Color(0xFFEF5350)
                            ),
                        contentAlignment = Alignment.Center
                    ) {}
                }
                if (detalle != detalles.last()) {
                    Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Leyenda
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Leyenda de Indicador",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LegendItem(color = Color(0xFF4CAF50), label = "Cumple (≥80%)")
                        LegendItem(color = Color(0xFFFFA726), label = "Alerta (70-79%)")
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LegendItem(color = Color(0xFFEF5350), label = "Incumple (<70%)")
                        LegendItem(color = Color(0xFF9E9E9E), label = "Sin dato")
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 10.sp, color = TextSoft)
    }
}

@Composable
private fun AnalisisIncumplimientosCard(
    selectedSlaType: String,
    totalIncumplimientos: Int,
    retrasoPromedio: Int,
    retrasoMaximo: Int,
    totalSolicitudes: Int,
    diasUmbral: Int,
    incumplimientosPorBloque: List<IncumplimientoDetalle>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, AlertBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header naranja de alerta
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AlertBackground, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFE65100),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Análisis de Incumplimientos - $selectedSlaType",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // KPIs de incumplimientos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IncumplimientoKpi(
                    title = "Total Incumplimientos",
                    value = "$totalIncumplimientos",
                    subtitle = "de $totalSolicitudes solicitudes",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IncumplimientoKpi(
                    title = "Retraso Promedio",
                    value = "$retrasoPromedio días",
                    subtitle = "sobre umbral de $diasUmbral días",
                    modifier = Modifier.weight(1f)
                )
                IncumplimientoKpi(
                    title = "Retraso Máximo",
                    value = "$retrasoMaximo días",
                    subtitle = "caso más crítico",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tabla de incumplimientos por bloque
            Text(
                text = "Incumplimientos por Bloque Tech",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Encabezados
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightLilac, RoundedCornerShape(8.dp))
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("BLOQUE", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f))
                Text("INCUMP.", fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.weight(0.9f))
                Text("% TOTAL", fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.weight(0.8f))
                Text("RET. PROM.", fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Filas
            incumplimientosPorBloque.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item.bloqueTech, fontSize = 12.sp, modifier = Modifier.weight(1.3f))
                    Text("${item.incumplimientos}", fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(0.9f))
                    Text(
                        text = "${item.porcentajeDelTotal}%",
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        modifier = Modifier
                            .weight(0.8f)
                            .background(Color(0xFFEF5350), RoundedCornerShape(8.dp))
                            .padding(vertical = 4.dp)
                    )
                    Text("${item.retrasoPromedio} días", fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                }
                if (item != incumplimientosPorBloque.last()) {
                    Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
private fun IncumplimientoKpi(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8F0)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE0B2))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF5D4037),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color(0xFF795548),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ConfiguracionReporteCard(
    reportName: String,
    onReportNameChange: (String) -> Unit,
    sugerencia: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = HeaderBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Configuración del Reporte",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = HeaderBlue
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = reportName,
                onValueChange = onReportNameChange,
                label = { Text("Título del Reporte") },
                placeholder = { Text(sugerencia, fontSize = 12.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Description, contentDescription = null, tint = HeaderBlue)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HeaderBlue,
                    unfocusedBorderColor = CardBorderLilac
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sugerencia: $sugerencia",
                fontSize = 11.sp,
                color = TextSoft,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

@Composable
private fun BotonesAccionCard(
    onGenerarPdf: () -> Unit,
    onHistorial: () -> Unit,
    enabled: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onGenerarPdf,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentOrange,
                disabledContainerColor = Color(0xFFBDBDBD)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PictureAsPdf,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Generar Reporte PDF",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        OutlinedButton(
            onClick = onHistorial,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = HeaderBlue
            ),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, HeaderBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Historial",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ============================================
// PREVIEW
// ============================================

@Preview(showBackground = true)
@Composable
fun StatisticsScreenPreview() {
    SLATrackerAPPTheme {
        StatisticsScreen()
    }
}

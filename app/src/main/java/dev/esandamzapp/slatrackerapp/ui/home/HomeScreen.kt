package dev.esandamzapp.slatrackerapp.ui.home

import android.app.DatePickerDialog
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// --- Colores del Tema ---
val BgBody = Color(0xFFF3F4F6)
val BgCard = Color(0xFFFFFFFF)
val PrimaryDark = Color(0xFF1F2937)
val TextPrimary = Color(0xFF111827)
val TextSecondary = Color(0xFF6B7280)
val AccentBlue = Color(0xFF3B82F6)
val AccentOrange = Color(0xFFE67E22)
val StatusSuccess = Color(0xFF10B981)
val StatusWarning = Color(0xFFF59E0B)
val StatusDanger = Color(0xFFEF4444)
val StatusPurple = Color(0xFF8B5CF6)

@Composable
fun HomeScreen(navController: NavController, homeViewModel: HomeViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    val kpis by homeViewModel.kpis.collectAsState()
    val filters by homeViewModel.filters.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()

    val availableBlocks by homeViewModel.availableBlocks.collectAsState()
    val availableTypes by homeViewModel.availableTypes.collectAsState()
    val availablePriorities by homeViewModel.availablePriorities.collectAsState()

    Scaffold(containerColor = BgBody) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
            ) {
                DashboardHeader(notificationsCount = kpis.pendingRequests)

                Box(
                    modifier = Modifier
                        .offset(y = (-60).dp)
                        .padding(horizontal = 20.dp)
                ) {
                    AdvancedFilterSection(
                        filters = filters,
                        blocks = availableBlocks,
                        types = availableTypes,
                        priorities = availablePriorities,
                        onFiltersChanged = { homeViewModel.updateFilters(it) },
                        onToggleOption = homeViewModel::toggleFilterOption
                    )
                }

                Column(
                    modifier = Modifier
                        .offset(y = (-40).dp)
                        .padding(horizontal = 20.dp)
                ) {
                    if (kpis.typeMetrics.isNotEmpty()) {
                        kpis.typeMetrics.chunked(2).forEach { rowMetrics ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                rowMetrics.forEach { metric ->
                                    Box(modifier = Modifier.weight(1f)) { TypeMetricCard(metric) }
                                }
                                if (rowMetrics.size == 1) Spacer(modifier = Modifier.weight(1f))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            GlobalKpiCard(
                                "Eficacia Global",
                                String.format(Locale.US, "%.2f%%", kpis.globalEfficacy),
                                "Cumplimiento general",
                                Icons.Outlined.CheckCircle,
                                if(kpis.globalEfficacy >= 80) StatusSuccess else if(kpis.globalEfficacy >= 50) StatusWarning else StatusDanger
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            GlobalKpiCard(
                                "Total Solicitudes",
                                kpis.totalRequests.toString(),
                                "Período seleccionado",
                                Icons.Outlined.Description,
                                TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    ChartCard("Cumplimiento SLA por Rol", "Porcentaje de cumplimiento por área tecnológica") {
                        SimpleBarChart(kpis.complianceByBlock, AccentBlue)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    ChartCard("Cumplimiento SLA por Prioridad", "Porcentaje de cumplimiento según nivel de prioridad") {
                        SimpleBarChart(kpis.complianceByPriority, AccentOrange)
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.3f)).clickable(enabled = false) {}, contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentBlue, trackColor = Color.White)
                }
            }
        }
    }
}

@Composable
fun TypeMetricCard(metric: SlaMetric) {
    Card(colors = CardDefaults.cardColors(containerColor = BgCard), elevation = CardDefaults.cardElevation(2.dp), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(metric.label, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = TextSecondary, maxLines = 1)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(String.format(Locale.US, "%.2f", metric.percentage), style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold), color = TextPrimary)
                Icon(Icons.Default.SwapHoriz, null, tint = Color.LightGray, modifier = Modifier.size(24.dp))
            }
            Text("${metric.averageDays} días promedio", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF3F4F6))
            Spacer(modifier = Modifier.height(8.dp))
            Text("${metric.complyingCount} de ${metric.totalCount} cumplen SLA", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}

@Composable
fun GlobalKpiCard(title: String, value: String, subtitle: String, icon: ImageVector, iconColor: Color) {
    Card(colors = CardDefaults.cardColors(containerColor = BgCard), elevation = CardDefaults.cardElevation(2.dp), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = TextSecondary)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(value, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold), color = TextPrimary)
                Icon(icon, null, tint = iconColor.copy(0.8f), modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@Composable
fun ChartCard(title: String, subtitle: String, content: @Composable () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = BgCard), elevation = CardDefaults.cardElevation(2.dp), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) { content() }
        }
    }
}

@Composable
fun SimpleBarChart(data: Map<String, Int>, barColor: Color) {
    if (data.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Info, null, tint = TextSecondary.copy(0.5f))
                Text("No se encontraron datos", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
        return
    }
    val labels = data.keys.toList()
    Canvas(modifier = Modifier.fillMaxSize()) {
        val barWidth = size.width / (data.size * 2f)
        val spacing = size.width / (data.size + 1)
        data.entries.forEachIndexed { index, entry ->
            val percentage = entry.value / 100f
            val barHeight = size.height * percentage
            val xPos = (spacing * (index + 1)) - (barWidth / 2)
            val color = if (entry.value < 50) StatusDanger else barColor
            drawRect(color = color, topLeft = Offset(xPos, size.height - barHeight), size = Size(barWidth, barHeight))
        }
    }
    Row(modifier = Modifier.fillMaxSize().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.Bottom) {
        labels.forEach { Text(it.take(3), style = MaterialTheme.typography.labelSmall, color = TextSecondary) }
    }
}

@Composable
fun SimpleDonutChart(data: Map<String, Int>) {
    if (data.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Info, null, tint = TextSecondary.copy(0.5f))
                Text("No se encontraron datos", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
        return
    }
    val total = data.values.sum().toFloat()
    val keys = data.keys.toList()
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(160.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 40f
                var startAngle = -90f
                data.values.forEachIndexed { index, value ->
                    val sweepAngle = (value / total) * 360f
                    val color = when(keys[index]) {
                        "CUMPLE" -> StatusSuccess
                        "NO_CUMPLE" -> StatusDanger
                        "RIESGO" -> StatusDanger
                        "PENDIENTE" -> StatusWarning
                        else -> Color.Gray
                    }
                    drawArc(color, startAngle, sweepAngle, false, style = Stroke(strokeWidth))
                    startAngle += sweepAngle
                }
            }
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(total.toInt().toString(), fontWeight = FontWeight.Bold, fontSize = 24.sp, color = TextPrimary)
                Text("Total", fontSize = 10.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
fun DashboardHeader(notificationsCount: Int) {
    Box(
        modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF111827), Color(0xFF374151)))).padding(24.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column {
                Text("Singula", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                Text("Dashboard SLA • RRHH", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF9CA3AF))
            }
            Box(Modifier.size(40.dp).background(Color.White.copy(0.1f), RoundedCornerShape(12.dp)).border(1.dp, Color.White.copy(0.2f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                BadgedBox({ if (notificationsCount > 0) Badge(containerColor = StatusDanger) { Text(notificationsCount.toString()) } }) {
                    Icon(Icons.Default.Notifications, null, tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun AdvancedFilterSection(
    filters: DashboardFilters,
    blocks: List<String>,
    types: List<String>,
    priorities: List<String>,
    onFiltersChanged: (DashboardFilters) -> Unit,
    onToggleOption: (List<String>, String) -> List<String>
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(targetValue = if (expanded) 180f else 0f)
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd MMM") }
    val context = LocalContext.current

    Card(colors = CardDefaults.cardColors(containerColor = BgCard), elevation = CardDefaults.cardElevation(8.dp), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(Modifier.fillMaxWidth().clickable { expanded = !expanded }.padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Menu, null, tint = PrimaryDark, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Filtros de Búsqueda", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                }
                Icon(Icons.Default.KeyboardArrowDown, "Expandir", tint = TextSecondary, modifier = Modifier.rotate(rotationState))
            }
            AnimatedVisibility(visible = expanded) {
                Column(Modifier.background(Color(0xFFFAFAFA)).padding(bottom = 16.dp)) {
                    Divider(color = Color(0xFFE5E7EB))

                    FilterSectionTitle("Rango de Fechas")
                    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DateSelectorButton("Desde", filters.startDate, dateFormatter, Modifier.weight(1f)) {
                            val d = filters.startDate
                            DatePickerDialog(context, { _, y, m, day -> onFiltersChanged(filters.copy(startDate = LocalDate.of(y, m + 1, day))) }, d.year, d.monthValue - 1, d.dayOfMonth).show()
                        }
                        DateSelectorButton("Hasta", filters.endDate, dateFormatter, Modifier.weight(1f)) {
                            val d = filters.endDate
                            DatePickerDialog(context, { _, y, m, day -> onFiltersChanged(filters.copy(endDate = LocalDate.of(y, m + 1, day))) }, d.year, d.monthValue - 1, d.dayOfMonth).show()
                        }
                    }

                    FilterSectionTitle("Bloque Tecnológico")
                    ChipGrid(blocks, filters.technologyBlock) { opt ->
                        onFiltersChanged(filters.copy(technologyBlock = onToggleOption(filters.technologyBlock, opt)))
                    }

                    FilterSectionTitle("Tipo Solicitud")
                    ChipGrid(types, filters.slaType) { opt ->
                        onFiltersChanged(filters.copy(slaType = onToggleOption(filters.slaType, opt)))
                    }

                    FilterSectionTitle("Prioridad")
                    ChipGrid(priorities, filters.priority) { opt ->
                        onFiltersChanged(filters.copy(priority = onToggleOption(filters.priority, opt)))
                    }

                    FilterSectionTitle("Cumplimiento")
                    // Se limita a las opciones binarias requeridas
                    val statusOptions = listOf("CUMPLE", "NO_CUMPLE")
                    ChipGrid(listOf("Todos") + statusOptions, filters.status) { opt ->
                        onFiltersChanged(filters.copy(status = onToggleOption(filters.status, opt)))
                    }

                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.End) {
                        TextButton({ onFiltersChanged(DashboardFilters()) }) { Text("Limpiar", color = TextSecondary) }
                        Button({ expanded = false }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark), shape = RoundedCornerShape(8.dp)) { Text("Aplicar Filtros") }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterSectionTitle(title: String) {
    Text(title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp), color = TextSecondary, modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp))
}

@Composable
fun DateSelectorButton(label: String, date: LocalDate, formatter: DateTimeFormatter, modifier: Modifier, onClick: () -> Unit) {
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary, modifier = Modifier.padding(bottom = 4.dp))
        Surface(onClick = onClick, shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color(0xFFE5E7EB)), color = Color.White) {
            Box(Modifier.padding(vertical = 10.dp, horizontal = 12.dp)) { Text(date.format(formatter), style = MaterialTheme.typography.bodyMedium, color = TextPrimary) }
        }
    }
}

@Composable
fun ChipGrid(options: List<String>, selectedOptions: List<String>, onOptionSelected: (String) -> Unit) {
    LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(options) { option ->
            val isSelected = selectedOptions.contains(option)
            val backgroundColor = if (isSelected) PrimaryDark else Color.White
            val contentColor = if (isSelected) Color.White else TextSecondary
            val borderColor = if (isSelected) PrimaryDark else Color(0xFFE5E7EB)
            Surface(shape = RoundedCornerShape(6.dp), color = backgroundColor, border = BorderStroke(1.dp, borderColor), modifier = Modifier.clickable { onOptionSelected(option) }) {
                Text(option.replace("_", " "), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = contentColor, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
            }
        }
    }
}
package dev.esandamzapp.slatrackerapp.ui.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape

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
import dev.esandamzapp.slatrackerapp.ui.statistics.ReportPreviewScreen
import dev.esandamzapp.slatrackerapp.ui.theme.SLATrackerAPPTheme


// Colores inspirados en tu Figma (azul + naranja + lilas)
private val HeaderBlue = Color(0xFF071C4D)
private val AccentOrange = Color(0xFFFF7A00)
private val LightLilac = Color(0xFFF6F2FF)
private val CardBorderLilac = Color(0xFFE3DFF5)
private val KpiBackground = Color(0xFFF6F2FF)
private val ChipUnselected = Color(0xFFE0E0E0)
private val TextSoft = Color(0xFF666666)

@Composable
fun StatisticsScreen() {
    var selectedSLA by remember { mutableStateOf("Todos") }
    var selectedRole by remember { mutableStateOf("Todos") }
    var showReportPreview by remember { mutableStateOf(false) }

    // Mock de datos
    val mockCumplimiento = 63
    val mockPromedioDias = 33
    val mockTotal = 8

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8FF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // ---------- HEADER AZUL (ESTADÍSTICAS) ----------
            StatisticsHeader()

            // ---------- CONTENIDO SCROLLEABLE ----------
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Filtros
                item {
                    FiltersCard(
                        selectedSLA = selectedSLA,
                        onSLAChange = { selectedSLA = it },
                        selectedRole = selectedRole,
                        onRoleChange = { selectedRole = it }
                    )
                }

                // KPIs
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        KpiCard(
                            title = "Tasa de Cumplimiento",
                            value = "$mockCumplimiento%",
                            modifier = Modifier.weight(1f)
                        )
                        KpiCard(
                            title = "Promedio de Días",
                            value = "$mockPromedioDias",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Gráficos mock
                item {
                    ChartCard(
                        title = "Distribución General",
                        subtitle = "Cumple vs No Cumple"
                    )
                }

                item {
                    ChartCard(
                        title = "Cumplimiento por Tipo de SLA",
                        subtitle = "SLA1 vs SLA2"
                    )
                }

                // Resumen ejecutivo
                item {
                    ExecutiveSummaryCard(
                        total = mockTotal,
                        cumplimiento = mockCumplimiento,
                        promedio = mockPromedioDias
                    )
                }

                // Tarjeta de Generar reporte
                item {
                    GenerateReportCard(
                        registros = mockTotal,
                        cumplimiento = mockCumplimiento,
                        onClick = { showReportPreview = true }
                    )
                }
            }
        }

        // ---------- OVERLAY PARA PREVISUALIZACIÓN DE REPORTE ----------
        if (showReportPreview) {
            Surface(
                color = Color.Black.copy(alpha = 0.35f),
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    ReportPreviewScreen(
                        onClose = { showReportPreview = false },
                        onExportPDF = { /* TODO: lógica futura */ },
                        onSendEmail = { /* TODO: lógica futura */ }
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// HEADER
// ----------------------------------------------------

@Composable
private fun StatisticsHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(HeaderBlue)
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Text(
                text = "Estadísticas",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Análisis de cumplimiento SLA",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp
            )
        }

        // Icono de notificaciones + badge (mock)
        Box(
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🔔", fontSize = 18.sp)
            }

            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF4B4B))
                    .align(Alignment.TopEnd),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "4",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ----------------------------------------------------
// FILTROS
// ----------------------------------------------------

@Composable
private fun FiltersCard(
    selectedSLA: String,
    onSLAChange: (String) -> Unit,
    selectedRole: String,
    onRoleChange: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightLilac
        ),
        border = CardDefaults.outlinedCardBorder().copy(width = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Filtros",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            // Tipo SLA
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Tipo SLA", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        text = "Todos",
                        selected = selectedSLA == "Todos",
                        onClick = { onSLAChange("Todos") }
                    )
                    FilterChip(
                        text = "SLA1",
                        selected = selectedSLA == "SLA1",
                        onClick = { onSLAChange("SLA1") }
                    )
                    FilterChip(
                        text = "SLA2",
                        selected = selectedSLA == "SLA2",
                        onClick = { onSLAChange("SLA2") }
                    )
                }
            }

            // Rol / Área
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Rol / Área", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                FilterChip(
                    text = selectedRole,
                    selected = true,
                    onClick = { /* luego abrirá un dropdown */ }
                )
            }
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (selected) AccentOrange else ChipUnselected
    val txtColor = if (selected) Color.White else Color(0xFF333333)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(999.dp),
        color = bg
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
            color = txtColor,
            fontSize = 14.sp
        )
    }
}

// ----------------------------------------------------
// KPIs
// ----------------------------------------------------

@Composable
private fun KpiCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = KpiBackground
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = TextSoft
            )
            Text(
                text = value,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ----------------------------------------------------
// CHART CARDS (mock)
// ----------------------------------------------------

@Composable
private fun ChartCard(
    title: String,
    subtitle: String
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightLilac
        ),
        border = CardDefaults.outlinedCardBorder().copy(width = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(min = 160.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                subtitle,
                fontSize = 13.sp,
                color = TextSoft
            )
            Spacer(Modifier.height(16.dp))
            // Área del gráfico mock
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Gráfico mock aquí",
                    color = TextSoft.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            }
        }
    }
}

// ----------------------------------------------------
// RESUMEN EJECUTIVO
// ----------------------------------------------------

@Composable
private fun ExecutiveSummaryCard(
    total: Int,
    cumplimiento: Int,
    promedio: Int
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF7ED)
        ),
        border = CardDefaults.outlinedCardBorder().copy(width = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Resumen Ejecutivo",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "• Total de solicitudes procesadas: $total",
                fontSize = 14.sp
            )
            Text(
                text = "• Tasa global de cumplimiento: $cumplimiento%",
                fontSize = 14.sp
            )
            Text(
                text = "• Tiempo promedio de contratación: $promedio días",
                fontSize = 14.sp
            )
            Text(
                text = "• Se requiere mejorar los tiempos de contratación",
                fontSize = 14.sp
            )
        }
    }
}

// ----------------------------------------------------
// TARJETA GENERAR REPORTE
// ----------------------------------------------------

@Composable
private fun GenerateReportCard(
    registros: Int,
    cumplimiento: Int,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, CardBorderLilac)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Generar Reporte SLA",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "Exportar o compartir análisis de cumplimiento",
                fontSize = 13.sp,
                color = TextSoft
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Registros filtrados",
                        fontSize = 13.sp,
                        color = TextSoft
                    )
                    Text(
                        registros.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Tasa cumplimiento",
                        fontSize = 13.sp,
                        color = TextSoft
                    )
                    Text(
                        "$cumplimiento%",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentOrange
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = "Generar reporte",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ----------------------------------------------------
// PREVIEW
// ----------------------------------------------------

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StatisticsScreenPreview() {
    SLATrackerAPPTheme {
        StatisticsScreen()
    }
}

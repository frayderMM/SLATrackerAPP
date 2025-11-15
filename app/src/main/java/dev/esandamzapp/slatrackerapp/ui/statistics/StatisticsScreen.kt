package com.example.app.ui.statistics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// ───────────────────────────────────────────────
// MAIN SCREEN
// ───────────────────────────────────────────────
@Composable
fun StatisticsScreen() {

    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .background(Color(0xFFF7F7F7))
    ) {

        HeaderSection()

        Spacer(Modifier.height(12.dp))

        FilterCard()

        Spacer(Modifier.height(8.dp))

        SummaryIndicators()

        Spacer(Modifier.height(12.dp))

        PieChartSection()

        Spacer(Modifier.height(12.dp))

        BarChartSection()

        Spacer(Modifier.height(16.dp))

        ExecutiveSummaryCard()

        Spacer(Modifier.height(12.dp))

        GenerateReportCard()

        Spacer(Modifier.height(20.dp))
    }
}

// ───────────────────────────────────────────────
// HEADER
// ───────────────────────────────────────────────

@Composable
fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0D1E44))
            .padding(20.dp)
    ) {
        Column {
            Text(
                "Estadísticas",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                "Análisis de cumplimiento SLA",
                color = Color(0xFFDCE6FF),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// ───────────────────────────────────────────────
// FILTROS
// ───────────────────────────────────────────────

@Composable
fun FilterCard() {

    var selectedSLA by remember { mutableStateOf("Todos") }
    var selectedRole by remember { mutableStateOf("Todos los roles") }

    Card(
        modifier = Modifier.padding(16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(20.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FilterAlt, "Filter", tint = Color(0xFFFF7A00))
                Spacer(Modifier.width(6.dp))
                Text("Filtros", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                DateInput("Desde")
                DateInput("Hasta")
            }

            Spacer(Modifier.height(12.dp))

            Text("Tipo SLA", style = MaterialTheme.typography.labelMedium)

            Spacer(Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChipM3(
                    label = "Todos",
                    selected = selectedSLA == "Todos",
                    onSelect = { selectedSLA = "Todos" }
                )
                FilterChipM3(
                    label = "SLA1",
                    selected = selectedSLA == "SLA1",
                    onSelect = { selectedSLA = "SLA1" }
                )
                FilterChipM3(
                    label = "SLA2",
                    selected = selectedSLA == "SLA2",
                    onSelect = { selectedSLA = "SLA2" }
                )
            }

            Spacer(Modifier.height(16.dp))

            Text("Rol/Área", style = MaterialTheme.typography.labelMedium)

            OutlinedTextField(
                value = selectedRole,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(Color(0xFFFF7A00)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Aplicar filtros")
                }

                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Limpiar")
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Mostrando 8 de 8 registros", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun DateInput(label: String) {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        readOnly = true,
        modifier = Modifier.width(150.dp),
        shape = RoundedCornerShape(12.dp),
        label = { Text(label) },
        trailingIcon = { Icon(Icons.Default.CalendarMonth, null) }
    )
}

@Composable
fun FilterChipM3(label: String, selected: Boolean, onSelect: () -> Unit) {
    AssistChip(
        onClick = onSelect,
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) Color(0xFFFF7A00) else Color.White,
            labelColor = if (selected) Color.White else Color.Black
        )
    )
}

// ───────────────────────────────────────────────
// INDICADORES
// ───────────────────────────────────────────────

@Composable
fun SummaryIndicators() {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        IndicatorCard("Tasa de Cumplimiento", "63%")
        IndicatorCard("Promedio de Días", "33")
    }
}

@Composable
fun IndicatorCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .width(170.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

// ───────────────────────────────────────────────
// PIE CHART
// ───────────────────────────────────────────────

@Composable
fun PieChartSection() {

    Card(
        modifier = Modifier.padding(16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(20.dp)) {

            PieChart(
                percent = 0.63f,
                colorA = Color(0xFF2ECC71),
                colorB = Color(0xFFE74C3C)
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("5", fontWeight = FontWeight.Bold)
                    Text("Cumple SLA")
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("3", fontWeight = FontWeight.Bold)
                    Text("No Cumple SLA")
                }
            }
        }
    }
}

@Composable
fun PieChart(percent: Float, colorA: Color, colorB: Color) {
    Canvas(modifier = Modifier.size(180.dp)) {
        val sweep = percent * 360f
        drawArc(colorA, startAngle = 0f, sweepAngle = sweep, useCenter = true)
        drawArc(
            color = colorB,
            startAngle = sweep,
            sweepAngle = 360f - sweep,
            useCenter = true
        )
    }
}

// ───────────────────────────────────────────────
// BAR CHART
// ───────────────────────────────────────────────

@Composable
fun BarChartSection() {
    Card(
        modifier = Modifier.padding(16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(20.dp)) {

            Text(
                "Cumplimiento por Tipo de SLA",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(20.dp))

            Canvas(modifier = Modifier.height(180.dp).fillMaxWidth()) {

                val barWidth = 70f

                // SLA1
                drawRect(Color(0xFF2ECC71), topLeft = Offset(100f, 20f), size = Size(barWidth, 200f))
                drawRect(Color(0xFFE74C3C), topLeft = Offset(180f, 140f), size = Size(barWidth, 80f))

                // SLA2
                drawRect(Color(0xFF2ECC71), topLeft = Offset(300f, 140f), size = Size(barWidth, 80f))
                drawRect(Color(0xFFE74C3C), topLeft = Offset(380f, 80f), size = Size(barWidth, 140f))
            }
        }
    }
}

// ───────────────────────────────────────────────
// RESUMEN EJECUTIVO
// ───────────────────────────────────────────────

@Composable
fun ExecutiveSummaryCard() {
    Card(
        modifier = Modifier.padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFFF7A00))
    ) {
        Column(Modifier.padding(20.dp)) {

            Text("Resumen Ejecutivo", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Bullet("Total de solicitudes procesadas: 8")
                Bullet("Tasa global de cumplimiento: 63%")
                Bullet("Tiempo promedio de contratación: 33 días")
                Bullet("Se requiere mejorar los tiempos de contratación")
            }
        }
    }
}

@Composable
fun Bullet(text: String) {
    Row {
        Text("• ", fontWeight = FontWeight.Bold)
        Text(text)
    }
}

// ───────────────────────────────────────────────
// GENERAR REPORTE
// ───────────────────────────────────────────────

@Composable
fun GenerateReportCard() {

    Card(
        modifier = Modifier.padding(16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(20.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.InsertDriveFile, null, tint = Color(0xFFFF7A00))
                Spacer(Modifier.width(8.dp))
                Text("Generar Reporte SLA", style = MaterialTheme.typography.titleMedium)
            }

            Text("Exportar o compartir análisis de cumplimiento")

            Spacer(Modifier.height(16.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Registros filtrados", fontWeight = FontWeight.Bold)
                    Text("8")
                }
                Column {
                    Text("Tasa cumplimiento", fontWeight = FontWeight.Bold)
                    Text("63%", color = Color(0xFFE74C3C))
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(Color(0xFFFF7A00)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Generar reporte")
            }
        }
    }
}

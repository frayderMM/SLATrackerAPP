package dev.esandamzapp.slatrackerapp.ui.prediction

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.esandamzapp.slatrackerapp.ui.theme.SLATrackerAPPTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictionScreen() {
    var requestDate by remember { mutableStateOf("2023-10-01") }
    var entryDate by remember { mutableStateOf("2023-10-04") }
    var slaType by remember { mutableStateOf("SLA1") }
    var simulationResult by remember { mutableStateOf<String?>(null) }
    var simulationColor by remember { mutableStateOf(Color.Gray) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F9)) // Fondo Premium
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        Spacer(modifier = Modifier.height(10.dp))

        // HEADER
        Text(
            text = "Predicción de SLA",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222)
            ),
            textAlign = TextAlign.Center
        )

        // ======================================================
        // US-016: PREDICCIÓN DE CUMPLIMIENTO (GRÁFICO)
        // ======================================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Tendencia de Cumplimiento",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    "Análisis basado en regresión lineal de los últimos 6 meses.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // GRÁFICO CUSTOM CON CANVAS
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(Color(0xFFFAFAFA), RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SlaTrendChart()
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Leyenda
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFF3280C4), RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Histórico", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(modifier = Modifier.size(10.dp).background(Color.Red, RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tendencia (Regresión)", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        // ======================================================
        // US-017: SIMULAR ESCENARIOS
        // ======================================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Simulador de Escenarios",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    "Modifica fechas hipotéticas para estimar el cumplimiento.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // INPUTS
                OutlinedTextField(
                    value = requestDate,
                    onValueChange = { requestDate = it },
                    label = { Text("Fecha Solicitud (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF3280C4),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = entryDate,
                    onValueChange = { entryDate = it },
                    label = { Text("Fecha Ingreso (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF3280C4),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Selector simple de SLA (Podría ser un Dropdown, usaremos botones por simplicidad)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Tipo SLA:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.width(10.dp))
                    FilterChip(
                        selected = slaType == "SLA1",
                        onClick = { slaType = "SLA1" },
                        label = { Text("SLA 1 (5 días)") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = slaType == "SLA2",
                        onClick = { slaType = "SLA2" },
                        label = { Text("SLA 2 (10 días)") }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // BOTÓN SIMULAR
                Button(
                    onClick = {
                        val result = calculateCompliance(requestDate, entryDate, slaType)
                        simulationResult = result.first
                        simulationColor = result.second
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3280C4)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Simular Resultado", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                // RESULTADO
                simulationResult?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(simulationColor.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                            .border(1.dp, simulationColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = it,
                            color = simulationColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

// ===============================================================
// LÓGICA DE NEGOCIO (SIMULACIÓN)
// ===============================================================
fun calculateCompliance(reqDate: String, entDate: String, type: String): Pair<String, Color> {
    try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val start = LocalDate.parse(reqDate, formatter)
        val end = LocalDate.parse(entDate, formatter)

        if (end.isBefore(start)) {
            return "Fecha de ingreso no puede ser anterior" to Color.Red
        }

        val days = ChronoUnit.DAYS.between(start, end)
        val limit = if (type == "SLA1") 5 else 10

        return if (days <= limit) {
            "CUMPLE (Días: $days / Límite: $limit)" to Color(0xFF2E7D32) // Verde
        } else {
            "NO CUMPLE (Días: $days / Límite: $limit)" to Color(0xFFC62828) // Rojo
        }

    } catch (e: Exception) {
        return "Formato de fecha inválido (Use YYYY-MM-DD)" to Color.Gray
    }
}

// ===============================================================
// COMPONENTE DE GRÁFICO (CANVAS)
// ===============================================================
@Composable
fun SlaTrendChart() {
    // Datos Mock: (Mes, % Cumplimiento)
    val dataPoints = listOf(
        0f to 85f,  // Mes 1
        1f to 82f,  // Mes 2
        2f to 78f,  // Mes 3
        3f to 88f,  // Mes 4
        4f to 75f,  // Mes 5
        5f to 70f   // Mes 6 (Actual)
    )

    Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
        val width = size.width
        val height = size.height
        val maxX = 5f
        val maxY = 100f

        // Ejes
        drawLine(
            color = Color.LightGray,
            start = Offset(0f, height),
            end = Offset(width, height),
            strokeWidth = 2f
        )
        drawLine(
            color = Color.LightGray,
            start = Offset(0f, 0f),
            end = Offset(0f, height),
            strokeWidth = 2f
        )

        // Puntos y Línea de Datos
        val path = Path()
        dataPoints.forEachIndexed { index, point ->
            val x = (point.first / maxX) * width
            val y = height - (point.second / maxY) * height

            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)

            // Dibujar punto
            drawCircle(
                color = Color(0xFF3280C4),
                radius = 8f,
                center = Offset(x, y)
            )
        }
        drawPath(
            path = path,
            color = Color(0xFF3280C4),
            style = Stroke(width = 4f)
        )

        // REGRESIÓN LINEAL SIMPLE (MOCK CALCULATION)
        // y = mx + b
        // Calculamos m y b basándonos en los puntos (simplificado)
        val n = dataPoints.size
        val sumX = dataPoints.sumOf { it.first.toDouble() }
        val sumY = dataPoints.sumOf { it.second.toDouble() }
        val sumXY = dataPoints.sumOf { (it.first * it.second).toDouble() }
        val sumXX = dataPoints.sumOf { (it.first * it.first).toDouble() }

        val m = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX)
        val b = (sumY - m * sumX) / n

        // Dibujar línea de tendencia (extendida un poco)
        val startY = height - (b / maxY * height).toFloat()
        val endXVal = 6f // Predecir mes 7
        val endYVal = (m * endXVal + b).toFloat()
        val endX = (endXVal / maxX) * width
        val endY = height - (endYVal / maxY * height)

        drawLine(
            color = Color.Red.copy(alpha = 0.7f),
            start = Offset(0f, startY),
            end = Offset(endX, endY.toFloat()),
            strokeWidth = 4f,
            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PredictionScreenPreview() {
    SLATrackerAPPTheme {
        PredictionScreen()
    }
}

package com.example.app.ui.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import java.time.LocalDate

// =======================================================
// ⭐ HOME SCREEN COMPLETO
// =======================================================
@Composable
fun HomeScreen() {

    val scroll = rememberScrollState()

    // 🔥 Ahora dinámico, escalable y móvil-friendly
    var selectedFilter by remember { mutableStateOf("Todos") }

    // Puedes tener 100 SLA y va perfecto
    val filterOptions = listOf(
        "Todos", "SLA1", "SLA2", "SLA3", "SLA4", "SLA5",
        "Cumple", "No Cumple", "Pendiente", "Escalado"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .background(Color(0xFFF8F9FF))
    ) {
        HeaderSection()

        FilterSection(
            selectedFilter = selectedFilter,
            filters = filterOptions,
            onFilterChange = { selectedFilter = it }
        )

        ImportExcelButton()

        Spacer(Modifier.height(12.dp))

        StatusRow()

        PredictionCard()

        RecommendationCard()

        RequestsListSection()

        Spacer(Modifier.height(80.dp))
    }
}

// =======================================================
// ⭐ HEADER
// =======================================================
@Composable
fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0F265C), Color(0xFF0A1C47))
                )
            )
            .padding(20.dp)
    ) {

        Column {
            Text(
                "Dashboard SLA",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Monitoreo de solicitudes de personal",
                color = Color(0xFFDFE6FF),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 12.dp, end = 4.dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.Red),
            contentAlignment = Alignment.Center
        ) {
            Text("4", color = Color.White, fontSize = 12.sp)
        }
    }
}

// =======================================================
// ⭐ FILTRO OPTIMIZADO PARA MÓVIL (LazyRow)
// =======================================================
@Composable
fun FilterSection(
    selectedFilter: String,
    filters: List<String>,
    onFilterChange: (String) -> Unit
) {

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .offset(y = (-18).dp)
    ) {

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF10265B)),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(Modifier.padding(20.dp)) {

                Text("Filtrar por:", color = Color.White)

                Spacer(Modifier.height(14.dp))

                // 🔥 NUEVO: LazyRow móvil-friendly
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filters) { option ->
                        FilterChipItem(
                            text = option,
                            selected = selectedFilter == option,
                            onClick = { onFilterChange(option) }
                        )
                    }
                }
            }
        }
    }
}

// =======================================================
// ⭐ CHIP MEJORADO + RIPPLE + ELEVACIÓN
// =======================================================
@Composable
fun FilterChipItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    val bg = if (selected) Color.White else Color(0xFF253D7A)
    val fg = if (selected) Color.Black else Color.White
    val borderColor = if (selected) Color.White else Color(0xFF334B84)

    Surface(
        shape = RoundedCornerShape(50),
        color = bg,
        border = BorderStroke(1.dp, borderColor),
        onClick = onClick,
        tonalElevation = if (selected) 5.dp else 0.dp
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text,
                color = fg,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

// =======================================================
// ⭐ IMPORTAR EXCEL
// =======================================================
@Composable
fun ImportExcelButton() {
    Box(
        Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(
                BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(listOf(Color(0xFFEFBC82), Color(0xFFFCC57D)))
                ),
                RoundedCornerShape(10.dp)
            )
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("📥   Importar desde Excel", color = Color(0xFFB06C00), fontWeight = FontWeight.SemiBold)
    }
}

// =======================================================
// ⭐ STATUS
// =======================================================
@Composable
fun StatusRow() {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StatusBox(
            title = "Cumple",
            value = "5",
            total = "5",
            color = Color(0xFFFF7A00),
            icon = Icons.Default.Check
        )
        StatusBox(
            title = "No Cumple",
            value = "3",
            total = "3",
            color = Color(0xFFE63946),
            icon = Icons.Default.Close
        )
    }
}

@Composable
fun StatusBox(
    title: String,
    value: String,
    total: String,
    color: Color,
    icon: ImageVector
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, color),
        modifier = Modifier.width(160.dp)
    ) {

        Column(Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(Modifier.height(4.dp))

            Text(
                value,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color.Black
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("de $total total")
                Spacer(Modifier.width(6.dp))
                Icon(icon, null, tint = color)
            }
        }
    }
}

// =======================================================
// ⭐ PREDICCIÓN
// =======================================================
@Composable
fun PredictionCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF4FF)),
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
    ) {

        Column(Modifier.padding(18.dp)) {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Info, null, tint = Color(0xFF003A9B))
                    Spacer(Modifier.width(6.dp))
                    Column {
                        Text("Predicción de Cumplimiento", fontWeight = FontWeight.Bold)
                        Text("Basado en regresión lineal", fontSize = 12.sp)
                    }
                }
                Icon(Icons.Default.Close, null, tint = Color.Gray)
            }

            Spacer(Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E7FF)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(Modifier.padding(16.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Cumplimiento Proyectado")
                        BadgeRed("Riesgo Alto")
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        "25%",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE63946)
                    )

                    Text("próximo período", color = Color.Gray)
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SmallStatCard("Tendencia", "📉", "Deca yendo")
                SmallStatCard("Promedio Días", "⏱", "32.8 días")
            }
        }
    }
}

@Composable
fun BadgeRed(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFE63946))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, color = Color.White, fontSize = 11.sp)
    }
}

@Composable
fun SmallStatCard(title: String, emoji: String, value: String) {
    Card(
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E7FF)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.width(150.dp)
    ) {

        Column(Modifier.padding(14.dp)) {
            Text(title, fontSize = 13.sp)
            Spacer(Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(emoji)
                Spacer(Modifier.width(6.dp))
                Text(value, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// =======================================================
// ⭐ RECOMENDACIÓN
// =======================================================
@Composable
fun RecommendationCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFDCEAFF)),
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
    ) {

        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(Icons.Outlined.Info, null, tint = Color(0xFF0057FF))
            Spacer(Modifier.width(10.dp))

            Text(
                "Recomendación: Atención. El cumplimiento está disminuyendo. Revisa procesos de contratación.",
                color = Color(0xFF0A1C47)
            )
        }
    }

    Spacer(Modifier.height(20.dp))
}

// =======================================================
// ⭐ LISTA DE SOLICITUDES
// =======================================================
@Composable
fun RequestsListSection() {
    Column(Modifier.padding(horizontal = 20.dp)) {

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Todas las Solicitudes", fontWeight = FontWeight.Bold)
            Badge(containerColor = Color(0xFFFF7A00)) {
                Text("8", color = Color.White)
            }
        }

        Spacer(Modifier.height(16.dp))

        requestsFake().forEach {
            RequestItemCard(it)
            Spacer(Modifier.height(12.dp))
        }
    }
}

data class RequestData(
    val title: String,
    val sla: String,
    val days: Int,
    val date: String,
    val status: Boolean
)

fun requestsFake() = listOf(
    RequestData("Desarrollador Frontend Senior", "SLA1", 25, "14/9/2025", true),
    RequestData("Gerente de Recursos Humanos", "SLA2", 46, "19/9/2025", false),
    RequestData("Analista de Datos", "SLA1", 23, "24/9/2025", true),
    RequestData("Diseñador UX/UI", "SLA1", 19, "30/9/2025", true),
    RequestData("Contador Senior", "SLA2", 66, "9/9/2025", false),
    RequestData("Ingeniero DevOps", "SLA1", 23, "4/10/2025", true),
    RequestData("Asistente Administrativo", "SLA2", 27, "27/9/2025", true),
    RequestData("Product Manager", "SLA1", 33, "3/10/2025", false),
)

@Composable
fun RequestItemCard(data: RequestData) {

    val border = if (data.status) Color(0xFFFF7A00) else Color(0xFFE63946)
    val bg = if (data.status) Color(0xFFFFF4E9) else Color(0xFFFFE8E8)

    Card(
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(2.dp, border),
        colors = CardDefaults.cardColors(containerColor = bg),
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(Modifier.padding(18.dp)) {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(data.title, fontWeight = FontWeight.Bold)
                Icon(
                    if (data.status) Icons.Default.Check else Icons.Default.Close,
                    null,
                    tint = border
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SlaTag(data.sla)
                Text("${data.days} días", color = Color.DarkGray)
            }

            Spacer(Modifier.height(6.dp))

            Text("Solicitado: ${data.date}", color = Color.Gray, fontSize = 13.sp)

            Spacer(Modifier.height(12.dp))

            StatusBadge(data.status)
        }
    }
}

@Composable
fun SlaTag(sla: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFE3E9FF))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(sla, color = Color(0xFF1231AE), fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
    }
}

@Composable
fun StatusBadge(ok: Boolean) {
    val bg = if (ok) Color(0xFFFF7A00) else Color(0xFFE63946)
    val text = if (ok) "✓ Cumple" else "✗ No cumple"

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text, color = Color.White, fontSize = 12.sp)
    }
}

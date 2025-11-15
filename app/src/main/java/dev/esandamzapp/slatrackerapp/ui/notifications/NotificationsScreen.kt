package dev.esandamzapp.slatrackerapp.ui.notifications

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.esandamzapp.slatrackerapp.R

// ===============================
// MODELO
// ===============================

data class NotificationSLA(
    val title: String,
    val description: String,
    val date: String,
    val slaTag: String,
    val type: NotificationType
)

enum class NotificationType {
    DANGER, WARNING, SUCCESS, INFO
}

// ===============================
// MOCK DATA
// ===============================

val mockNotifications = listOf(
    NotificationSLA(
        "SLA No Cumplido",
        "Contador Senior excedió el límite por 21 días",
        "14 de noviembre de 2025",
        "SLA2",
        NotificationType.DANGER
    ),
    NotificationSLA(
        "SLA No Cumplido",
        "Product Manager excedió el límite por 3 días",
        "09 de noviembre de 2025",
        "SLA1",
        NotificationType.DANGER
    ),
    NotificationSLA(
        "SLA No Cumplido",
        "Gerente de Recursos Humanos excedió el límite por 1 día",
        "04 de noviembre de 2025",
        "SLA2",
        NotificationType.WARNING
    ),
    NotificationSLA(
        "SLA Cumplido",
        "Analista de Datos completado exitosamente",
        "17 de octubre de 2025",
        "SLA1",
        NotificationType.SUCCESS
    ),
    NotificationSLA(
        "SLA Por Vencer",
        "Desarrollador Frontend Senior tiene 5 días de margen",
        "09 de octubre de 2025",
        "SLA1",
        NotificationType.INFO
    )
)

// ===============================
// PANTALLA COMPLETA
// ===============================
@Composable
fun NotificationsScreen(navController: NavController) {

    Scaffold { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(inner)
        ) {

            NotificationHeader(navController)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(mockNotifications) { notif ->
                    NotificationCardSLA(notif)
                }
            }
        }
    }
}

// ===============================
// ENCABEZADO PREMIUM
// ===============================
@Composable
fun NotificationHeader(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0D1A42)) // azul premium corporativo
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "Volver",
                color = Color.White,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            "Notificaciones",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Alertas y actualizaciones de SLA",
            color = Color(0xFFBFD3F2),
            fontSize = 15.sp
        )
    }
}

// ===============================
// COLORES SEGÚN TIPO SLA
// ===============================

fun getSLAColors(type: NotificationType): Triple<Color, Color, Color> {

    return when (type) {

        NotificationType.DANGER -> Triple(
            Color(0xFFFFF0F0), // fondo
            Color(0xFFFFB8B8), // borde
            Color(0xFFC62828)  // icono
        )

        NotificationType.WARNING -> Triple(
            Color(0xFFFFF8E8),
            Color(0xFFFFD88D),
            Color(0xFFF57C00)
        )

        NotificationType.SUCCESS -> Triple(
            Color(0xFFEFFFF1),
            Color(0xFFB6DFBB),
            Color(0xFF2E7D32)
        )

        NotificationType.INFO -> Triple(
            Color(0xFFEAF3FF),
            Color(0xFFB7D3FF),
            Color(0xFF1565C0)
        )
    }
}

// ===============================
// BADGE SLA
// ===============================
@Composable
fun SLATag(tag: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            tag,
            color = color,
            fontSize = 12.sp
        )
    }
}

// ===============================
// CARD ESTILO IMAGEN
// ===============================
@Composable
fun NotificationCardSLA(notif: NotificationSLA) {

    val (bg, border, iconColor) = getSLAColors(notif.type)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = bg),
        border = BorderStroke(1.dp, border),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            // Primera fila
            Row(verticalAlignment = Alignment.CenterVertically) {

                Icon(
                    painter = painterResource(
                        id = when (notif.type) {
                            NotificationType.DANGER -> R.drawable.ic_warning
                            NotificationType.WARNING -> R.drawable.ic_warning
                            NotificationType.SUCCESS -> R.drawable.ic_check
                            NotificationType.INFO -> R.drawable.ic_info
                        }
                    ),
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    notif.title,
                    color = iconColor,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.width(6.dp))

                SLATag(notif.slaTag, iconColor)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                notif.description,
                fontSize = 15.sp,
                color = Color(0xFF3A3A3A)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                notif.date,
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
    }
}

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.esandamzapp.slatrackerapp.R
import dev.esandamzapp.slatrackerapp.data.remote.dto.AlertDto


// --- Modelo de datos para la UI ---
data class NotificationSLA(
    val title: String,
    val description: String,
    val date: String,
    val slaTag: String,
    val type: NotificationType
)

enum class NotificationType {
    DANGER, WARNING, SUCCESS
}

fun AlertDto.toNotificationSLA(): NotificationSLA {
    return NotificationSLA(
        title = nivel ?: "Notificación",
        description = mensaje ?: "Sin descripción",
        date = fechaCreacion ?: "",
        slaTag = "SLA$idTipoAlerta",
        type = when (idEstadoAlerta) {
            1 -> NotificationType.SUCCESS // Cumplido
            3 -> NotificationType.DANGER  // Incumplido
            else -> NotificationType.WARNING // Por Vencer y otros
        }
    )
}


@Composable
fun NotificationsScreen(
    navController: NavController,
    userId: Int
) {
    val viewModel: NotificationsViewModel = viewModel(factory = NotificationsViewModelFactory(userId))
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            NotificationHeader(navController)

            when (val state = uiState) {
                is NotificationUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is NotificationUiState.Success -> {
                    if (state.notifications.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text(
                                "No hay notificaciones sin leer por el momento.", 
                                textAlign = TextAlign.Center,
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
                        ) {
                            items(state.notifications) { notif ->
                                NotificationCardSLA(notif)
                            }
                        }
                    }
                }
                is NotificationUiState.Error -> {
                     Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            "Error al cargar notificaciones: ${state.message}",
                            textAlign = TextAlign.Center,
                            color = Color.Red
                        )
                    }
                }
            }
        }
    }
}

// ===============================
// COMPONENTES DE UI (Recreados)
// ===============================

@Composable
fun NotificationHeader(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0D1A42))
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
            Text("Volver", color = Color.White, fontSize = 15.sp)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text("Notificaciones", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("Alertas y actualizaciones de SLA", color = Color(0xFFBFD3F2), fontSize = 15.sp)
    }
}

fun getSLAColors(type: NotificationType): Triple<Color, Color, Color> {
    return when (type) {
        NotificationType.DANGER -> Triple(Color(0xFFFFF0F0), Color(0xFFFFB8B8), Color(0xFFC62828))
        NotificationType.WARNING -> Triple(Color(0xFFFFF8E8), Color(0xFFFFD88D), Color(0xFFF57C00))
        NotificationType.SUCCESS -> Triple(Color(0xFFEFFFF1), Color(0xFFB6DFBB), Color(0xFF2E7D32))
    }
}

@Composable
fun SLATag(tag: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(tag, color = color, fontSize = 12.sp)
    }
}

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(
                        id = when (notif.type) {
                            NotificationType.DANGER, NotificationType.WARNING -> R.drawable.ic_warning
                            NotificationType.SUCCESS -> R.drawable.ic_check
                        }
                    ),
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(notif.title, color = iconColor, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.width(6.dp))
                SLATag(notif.slaTag, iconColor)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(notif.description, fontSize = 15.sp, color = Color(0xFF3A3A3A))
            Spacer(modifier = Modifier.height(6.dp))
            Text(notif.date, fontSize = 13.sp, color = Color.Gray)
        }
    }
}

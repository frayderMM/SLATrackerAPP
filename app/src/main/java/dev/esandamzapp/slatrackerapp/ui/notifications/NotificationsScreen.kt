package dev.esandamzapp.slatrackerapp.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// Paleta de colores corporativa (TCS)
private val tcsNavyBlue = Color(0xFF002147)
private val tcsLightGray = Color(0xFFF5F7FA)

data class Notificacion(
    val titulo: String,
    val descripcion: String,
    val fecha: String,
    val tipo: TipoSLA
)

enum class TipoSLA { INCUMPLIDO, CUMPLIDO, POR_VENCER }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionesScreen(
    onBack: () -> Unit = {},
    viewModel: NotificationsViewModel = viewModel()
) {

    val notifications by viewModel.notifications.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Notificaciones", 
                        fontWeight = FontWeight.Bold
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = tcsNavyBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(tcsLightGray)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(notifications) { item ->
                SlaCard(item)
            }
        }
    }
}

@Composable
fun SlaCard(notificacion: Notificacion) {

    val bgColor = when (notificacion.tipo) {
        TipoSLA.INCUMPLIDO -> Color(0xFFFFF0F0)
        TipoSLA.CUMPLIDO -> Color(0xFFF0FFF4)
        TipoSLA.POR_VENCER -> Color(0xFFFFFBEB)
    }

    val textoColor = when (notificacion.tipo) {
        TipoSLA.INCUMPLIDO -> Color(0xFFC53030)
        TipoSLA.CUMPLIDO -> Color(0xFF2F855A)
        TipoSLA.POR_VENCER -> Color(0xFFB7791F)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notificacion.titulo,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textoColor
                )

                Text(
                    text = when (notificacion.tipo) {
                        TipoSLA.INCUMPLIDO -> "SLA2"
                        TipoSLA.CUMPLIDO -> "SLA1"
                        TipoSLA.POR_VENCER -> "SLA1"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = notificacion.descripcion, color = Color(0xFF444444), fontSize = 14.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = notificacion.fecha,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

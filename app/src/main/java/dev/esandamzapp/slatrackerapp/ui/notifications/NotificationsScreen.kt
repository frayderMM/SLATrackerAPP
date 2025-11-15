package dev.esandamzapp.slatrackerapp.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.Alignment


data class Notificacion(
    val titulo: String,
    val descripcion: String,
    val fecha: String,
    val tipo: TipoSLA
)

enum class TipoSLA { INCUMPLIDO, CUMPLIDO, POR_VENCER }

@Composable
fun NotificacionesScreen(
    onBack: () -> Unit = {}   // función para navegar atrás
) {

    val lista = listOf(
        Notificacion(
            "SLA No Cumplido",
            "Contador Senior excedió el límite por 21 días",
            "14 de noviembre de 2025",
            TipoSLA.INCUMPLIDO
        ),
        Notificacion(
            "SLA No Cumplido",
            "Product Manager excedió el límite por 3 días",
            "09 de noviembre de 2025",
            TipoSLA.INCUMPLIDO
        ),
        Notificacion(
            "SLA No Cumplido",
            "Gerente de Recursos Humanos excedió el límite por 1 día",
            "04 de noviembre de 2025",
            TipoSLA.INCUMPLIDO
        ),
        Notificacion(
            "SLA Cumplido",
            "Analista de Datos completado exitosamente",
            "17 de octubre de 2025",
            TipoSLA.CUMPLIDO
        ),
        Notificacion(
            "SLA Por Vencer",
            "Desarrollador Frontend Senior tiene 5 días de margen",
            "09 de octubre de 2025",
            TipoSLA.POR_VENCER
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F4F8))
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {

        // ------------------------------
        // BOTÓN VOLVER + TÍTULO
        // ------------------------------
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.height(70.dp))
            IconButton(onClick = { onBack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color(0xFF0A1F44)
                )
            }

            Column {
                Text(
                    text = "Notificaciones",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0A1F44)
                )
                Text(
                    text = "Alertas y actualizaciones de SLA",
                    fontSize = 13.sp,
                    color = Color(0xFF4A5568)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(lista) { item ->
                SlaCard(item)
                Spacer(modifier = Modifier.height(12.dp))
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
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
                    color = Color.Black,
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(text = notificacion.descripcion, color = Color(0xFF444444))

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = notificacion.fecha,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

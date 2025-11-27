package dev.esandamzapp.slatrackerapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Paleta de colores corporativa (TCS)
private val tcsNavyBlue = Color(0xFF002147)
private val tcsCyan = Color(0xFF00A9CE)
private val tcsLightGray = Color(0xFFF5F7FA)
private val textDark = Color(0xFF1E293B)

data class DashboardItem(
    val title: String,
    val icon: ImageVector,
    val action: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSla: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToLoadProcessing: () -> Unit
) {

    val dashboardItems = listOf(
        DashboardItem("Notificaciones", Icons.Default.Notifications, onNavigateToNotifications),
        DashboardItem("Ver SLAs", Icons.Default.FactCheck, onNavigateToSla),
        DashboardItem("Estadísticas", Icons.Default.BarChart, onNavigateToStatistics),
        DashboardItem("Procesar Carga", Icons.Default.UploadFile, onNavigateToLoadProcessing),
        DashboardItem("Ajustes", Icons.Default.Settings, onNavigateToSettings),
        DashboardItem("Mi Perfil", Icons.Default.AccountCircle, onNavigateToProfile)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "TCS Dashboard", 
                        fontWeight = FontWeight.Bold
                    ) 
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = tcsNavyBlue,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Perfil"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(tcsLightGray)
                .padding(paddingValues)
                .padding(8.dp) 
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(dashboardItems) { item ->
                    DashboardCard(item = item)
                }
            }
        }
    }
}

@Composable
fun DashboardCard(item: DashboardItem) {
    Card(
        modifier = Modifier
            .aspectRatio(1f) // Tarjetas cuadradas
            .clickable(onClick = item.action),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(tcsCyan.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = tcsCyan,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = item.title,
                fontWeight = FontWeight.Medium,
                color = textDark,
                fontSize = 16.sp
            )
        }
    }
}

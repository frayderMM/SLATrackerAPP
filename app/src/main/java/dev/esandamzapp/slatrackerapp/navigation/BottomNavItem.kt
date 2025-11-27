package dev.esandamzapp.slatrackerapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.List // <--- Agrega este (u otro que te guste)
// BORRA la línea de: import co.yml.charts.ui.barchart.BarChart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Dashboard : BottomNavItem("home", "Dashboard", Icons.Default.Home)

    // CAMBIO AQUÍ: Usa Icons.Default.List en lugar de BarChart
    object Estadisticas : BottomNavItem("statistics", "Estadísticas", Icons.Default.List)

    object Nuevo : BottomNavItem("newRequest", "Nuevo", Icons.Default.Add)
    object Perfil : BottomNavItem("profile", "Perfil", Icons.Default.Person)
    object Configuracion : BottomNavItem("configuration", "Configuración", Icons.Default.Settings)
}
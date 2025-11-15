package dev.esandamzapp.slatrackerapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Dashboard : BottomNavItem("home", "Dashboard", Icons.Default.Home)
    object Estadisticas : BottomNavItem("statistics", "Estadísticas", Icons.Default.BarChart)
    object Nuevo : BottomNavItem("newRequest", "Nuevo", Icons.Default.Add)
    object Perfil : BottomNavItem("profile", "Perfil", Icons.Default.Person)
}

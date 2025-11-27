package dev.esandamzapp.slatrackerapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Addchart
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Dashboard : BottomNavItem(
        route = "home",
        icon = Icons.Default.Dashboard,
        label = "Dashboard"
    )

    object Estadisticas : BottomNavItem(
        route = "statistics",
        icon = Icons.Default.Analytics,
        label = "Estadísticas"
    )

    object RegistroManual : BottomNavItem(
        route = "manual_registration",
        icon = Icons.Default.Addchart,
        label = "Registrar"
    )

    object Perfil : BottomNavItem(
        route = "profile",
        icon = Icons.Default.Person,
        label = "Perfil"
    )

    // La pantalla de predicción se puede añadir a la barra de navegación si se desea.
    // object Prediccion : BottomNavItem(
    //     route = "prediction",
    //     icon = Icons.Default.OnlinePrediction,
    //     label = "Predecir"
    // )
}

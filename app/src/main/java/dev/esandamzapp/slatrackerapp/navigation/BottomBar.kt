package dev.esandamzapp.slatrackerapp.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Estadisticas,
        BottomNavItem.RegistroManual, // Ruta actualizada
        BottomNavItem.Perfil
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Evita que se acumulen múltiples copias de la misma pantalla
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Lanza la pantalla como una única instancia
                        launchSingleTop = true
                        // Restaura el estado si se vuelve a la pantalla
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

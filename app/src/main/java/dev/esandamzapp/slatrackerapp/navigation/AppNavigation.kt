package dev.esandamzapp.slatrackerapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.esandamzapp.slatrackerapp.ui.auth.LoginScreen
import dev.esandamzapp.slatrackerapp.ui.configuration.ConfigurationScreen
import dev.esandamzapp.slatrackerapp.ui.home.HomeScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            // La BottomBar solo se muestra si la ruta actual no es "login"
            if (currentRoute != "login") {
                BottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {

            // Pantalla de Login
            composable("login") {
                // Usamos una lambda para navegar al home en caso de éxito
                LoginScreen(onLoginSuccess = {
                    navController.navigate("home") {
                        // Limpia el backstack para que el usuario no pueda volver al login
                        popUpTo("login") { inclusive = true }
                    }
                })
            }

            // Pantalla Principal (Dashboard)
            composable("home") {
                HomeScreen(navController = navController)
            }

            // Pantalla de Configuración
            composable("configuration") {
                ConfigurationScreen(navController = navController)
            }

            // Aquí puedes añadir otras pantallas que están en tu BottomBar, como "statistics" o "profile"
            // composable("statistics") { StatisticsScreen() }
            // composable("profile") { ProfileScreen(navController) }
        }
    }
}

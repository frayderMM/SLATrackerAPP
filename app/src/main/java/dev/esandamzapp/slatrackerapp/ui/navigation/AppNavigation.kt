package dev.esandamzapp.slatrackerapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.esandamzapp.slatrackerapp.ui.auth.LoginScreen
import dev.esandamzapp.slatrackerapp.ui.home.HomeScreen
import dev.esandamzapp.slatrackerapp.ui.loadProcessing.LoadProcessingScreen
import dev.esandamzapp.slatrackerapp.ui.notifications.NotificacionesScreen
import dev.esandamzapp.slatrackerapp.ui.profile.ProfileScreen
import dev.esandamzapp.slatrackerapp.ui.settings.SettingsScreen
import dev.esandamzapp.slatrackerapp.ui.sla.SlaScreen
import dev.esandamzapp.slatrackerapp.ui.statistics.StatisticsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppScreens.LoginScreen.route
    ) {
        composable(AppScreens.LoginScreen.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(AppScreens.HomeScreen.route) }
            )
        }
        composable(AppScreens.HomeScreen.route) {
            HomeScreen(
                onNavigateToNotifications = { navController.navigate(AppScreens.NotificationsScreen.route) },
                onNavigateToProfile = { navController.navigate(AppScreens.ProfileScreen.route) },
                onNavigateToSettings = { navController.navigate(AppScreens.SettingsScreen.route) },
                onNavigateToSla = { navController.navigate(AppScreens.SlaScreen.route) },
                onNavigateToStatistics = { navController.navigate(AppScreens.StatisticsScreen.route) },
                onNavigateToLoadProcessing = { navController.navigate(AppScreens.LoadProcessingScreen.route) }
            )
        }
        composable(AppScreens.NotificationsScreen.route) {
            NotificacionesScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(AppScreens.ProfileScreen.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(AppScreens.SettingsScreen.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(AppScreens.SlaScreen.route) {
            SlaScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(AppScreens.StatisticsScreen.route) {
            StatisticsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(AppScreens.LoadProcessingScreen.route) {
            LoadProcessingScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

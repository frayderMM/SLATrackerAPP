package dev.esandamzapp.slatrackerapp.ui.navigation

sealed class AppScreens(val route: String) {
    object LoginScreen : AppScreens("login_screen")
    object HomeScreen : AppScreens("home_screen")
    object LoadProcessingScreen : AppScreens("load_processing_screen")
    object NotificationsScreen : AppScreens("notifications_screen")
    object ProfileScreen : AppScreens("profile_screen")
    object SettingsScreen : AppScreens("settings_screen")
    object SlaScreen : AppScreens("sla_screen")
    object StatisticsScreen : AppScreens("statistics_screen")
}

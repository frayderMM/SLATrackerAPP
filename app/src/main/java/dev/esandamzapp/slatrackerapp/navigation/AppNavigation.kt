package dev.esandamzapp.slatrackerapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import dev.esandamzapp.slatrackerapp.ui.home.HomeScreen
import dev.esandamzapp.slatrackerapp.ui.statistics.StatisticsScreen
import dev.esandamzapp.slatrackerapp.ui.auth.LoginScreen
import dev.esandamzapp.slatrackerapp.ui.loadProcessing.ImportarDatosExcelScreen
import dev.esandamzapp.slatrackerapp.ui.notifications.NotificationsScreen
import dev.esandamzapp.slatrackerapp.ui.options.*
import dev.esandamzapp.slatrackerapp.ui.profile.ProfileScreen
import dev.esandamzapp.slatrackerapp.ui.reports.ReportsScreen
import dev.esandamzapp.slatrackerapp.ui.settings.SettingsScreen
import dev.esandamzapp.slatrackerapp.ui.sla.NewRequestScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != "login") {
                BottomBar(navController)
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {

            // ===============================
            // LOGIN
            // ===============================
            composable("login") {
                LoginScreen(
                    onLoginSuccess = { token, userId ->
                        navController.navigate("home/$token/$userId") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            // ===============================
            // HOME (recibe token + userId)
            // ===============================
            composable(
                route = "home/{token}/{userId}",
                arguments = listOf(
                    navArgument("token") { type = NavType.StringType },
                    navArgument("userId") { type = NavType.IntType }
                )
            ) { backStack ->

                val token = backStack.arguments?.getString("token")!!
                val userId = backStack.arguments?.getInt("userId")!!

                // Guardar datos globales para otras pantallas
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("token", token)

                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("userId", userId)

                HomeScreen(navController)
            }

            // ===============================
            // PROFILE (recupera token/userId desde SavedState)
            // ===============================
            composable("profile") {

                val token = navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<String>("token") ?: ""

                val userId = navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<Int>("userId") ?: 0

                ProfileScreen(
                    token = token,
                    userId = userId,
                    onNotifications = { navController.navigate("notifications") },
                    onReports = { navController.navigate("reports") },
                    onLinks = { navController.navigate("links") },
                    onSettings = { navController.navigate("settings") },
                    onSecurity = { navController.navigate("security") },
                    onHelpCenter = { navController.navigate("helpCenter") },
                    onReportProblem = { navController.navigate("reportProblem") },

                    onLogout = {
                        navController.navigate("login") {
                            popUpTo("home/{token}/{userId}") { inclusive = true }
                        }
                    }
                )
            }

            // ===============================
            // OTRAS PANTALLAS
            // ===============================
            composable("statistics") { 
                StatisticsScreen(
                    onHistoryClick = { navController.navigate("pdfHistory") }
                )
            }
            composable("pdfHistory") { 
                dev.esandamzapp.slatrackerapp.ui.statistics.PdfHistoryScreen(
                    onBackClick = { navController.navigateUp() }
                )
            }
            composable("newRequest") { NewRequestScreen() }
            composable("notifications") { NotificationsScreen(navController) }
            composable("reports") { ReportsScreen(navController) }
            composable("links") { LinksOfInterestScreen(navController) }
            composable("settings") { SettingsScreen(navController) }
            composable("security") { SecurityScreen(navController) }
            composable("helpCenter") { SupportScreen(navController) }
            composable("reportProblem") { ReportProblemScreen(navController) }
            composable("loadProcessing") { ImportarDatosExcelScreen(navController) }
        }
    }
}

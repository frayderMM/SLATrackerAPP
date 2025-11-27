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
import com.example.app.ui.home.HomeScreen

import dev.esandamzapp.slatrackerapp.ui.auth.LoginScreen
import dev.esandamzapp.slatrackerapp.ui.loadProcessing.ImportarDatosExcelScreen
import dev.esandamzapp.slatrackerapp.ui.notifications.NotificationsScreen
import dev.esandamzapp.slatrackerapp.ui.options.LinksOfInterestScreen
import dev.esandamzapp.slatrackerapp.ui.options.ReportProblemScreen
import dev.esandamzapp.slatrackerapp.ui.options.SecurityScreen
import dev.esandamzapp.slatrackerapp.ui.options.SupportScreen
import dev.esandamzapp.slatrackerapp.ui.profile.ProfileScreen
import dev.esandamzapp.slatrackerapp.ui.reports.ReportsScreen
import dev.esandamzapp.slatrackerapp.ui.settings.SettingsScreen
import dev.esandamzapp.slatrackerapp.ui.sla.NewRequestScreen
import dev.esandamzapp.slatrackerapp.ui.statistics.StatisticsScreen

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

            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            composable("home") { HomeScreen(navController) }

            composable("profile") {
                ProfileScreen(
                    onNotifications = { navController.navigate("notifications") },
                    onReports = { navController.navigate("reports") },
                    onLinks = { navController.navigate("links") },
                    onSettings = { navController.navigate("settings") },
                    onSecurity = { navController.navigate("security") },
                    onHelpCenter = { navController.navigate("helpCenter") },
                    onReportProblem = { navController.navigate("reportProblem") },
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }

            composable("statistics") { StatisticsScreen() }
            composable("newRequest") { NewRequestScreen() }
            composable("notifications") { NotificationsScreen(navController) }
            composable("reports") { ReportsScreen(navController) }
            composable("links") { LinksOfInterestScreen(navController) }
            composable("settings") { SettingsScreen(navController) }
            composable("security") { SecurityScreen(navController) }
            composable("helpCenter") { SupportScreen(navController) }
            composable("reportProblem") { ReportProblemScreen(navController) }
            composable("loadProcessing") {
                ImportarDatosExcelScreen(navController)
            }
        }
    }
}

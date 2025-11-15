package dev.esandamzapp.slatrackerapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.app.ui.home.HomeScreen
import com.example.app.ui.statistics.StatisticsScreen
import dev.esandamzapp.slatrackerapp.navigation.BottomBar
import dev.esandamzapp.slatrackerapp.ui.auth.LoginScreen
import dev.esandamzapp.slatrackerapp.ui.notifications.NotificationsScreen
import dev.esandamzapp.slatrackerapp.ui.options.LinksOfInterestScreen
import dev.esandamzapp.slatrackerapp.ui.options.ReportProblemScreen
import dev.esandamzapp.slatrackerapp.ui.options.SecurityScreen
import dev.esandamzapp.slatrackerapp.ui.options.SupportScreen
import dev.esandamzapp.slatrackerapp.ui.profile.ProfileScreen
import dev.esandamzapp.slatrackerapp.ui.reports.ReportsScreen
import dev.esandamzapp.slatrackerapp.ui.settings.SettingsScreen
import dev.esandamzapp.slatrackerapp.ui.sla.NewRequestScreen


@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(navController)
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)   // ← FIX
        ) {

            composable("home") { HomeScreen() }
            composable("login") {  LoginScreen()}

            composable("profile") {
                ProfileScreen(
                    onNotifications = { navController.navigate("notifications") },
                    onReports = { navController.navigate("reports") },
                    onLinks = { navController.navigate("links") },
                    onSettings = { navController.navigate("settings") },
                    onSecurity = { navController.navigate("security") },
                    onHelpCenter = { navController.navigate("helpCenter") },
                    onReportProblem = { navController.navigate("reportProblem") },
                    onLogout = { /* logout */ }
                )
            }

            composable("newRequest") { NewRequestScreen() }
            composable("statistics") { StatisticsScreen() }

            // ==================================
            // ⚡ PANTALLAS NUEVAS
            // ==================================

            composable("notifications") { NotificationsScreen(navController) }
            composable("reports") { ReportsScreen(navController) }
            composable("links") { LinksOfInterestScreen(navController) }
            composable("settings") { SettingsScreen(navController) }
            composable("security") { SecurityScreen(navController) }
            composable("helpCenter") { SupportScreen(navController) }
            composable("reportProblem") { ReportProblemScreen(navController) }
        }
    }
}

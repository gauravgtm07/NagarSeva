package com.nagarseva.app.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nagarseva.app.ui.auth.*
import com.nagarseva.app.ui.home.HomeScreen
import com.nagarseva.app.ui.report.*
import com.nagarseva.app.ui.confirmation.ConfirmationScreen
import com.nagarseva.app.ui.tracker.TrackerScreen
import com.nagarseva.app.ui.reports.MyReportsScreen
import com.nagarseva.app.ui.reports.ReportDetailScreen
import com.nagarseva.app.ui.profile.ProfileScreen
import com.nagarseva.app.ui.profile.EditProfileScreen
import com.nagarseva.app.ui.services.ServicesScreen
import com.nagarseva.app.ui.notifications.NotificationsScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object ResetPassword : Screen("reset_password")
    object Home : Screen("home")
    object ReportStep1 : Screen("report_step1")
    object ReportStep2 : Screen(
        "report_step2/{issueTitle}/{issueType}/{latitude}/{longitude}/{address}"
    ) {
        fun createRoute(
            issueTitle: String,
            issueType: String,
            latitude: Float,
            longitude: Float,
            address: String
        ): String {
            return "report_step2/" +
                "${Uri.encode(issueTitle)}/" +
                "${Uri.encode(issueType)}/" +
                "$latitude/$longitude/" +
                Uri.encode(address)
        }
    }
    object Camera : Screen("camera")
    object Confirmation : Screen(
        "confirmation/{ticketId}"
    ) {
        fun createRoute(ticketId: String) =
            "confirmation/${Uri.encode(ticketId)}"
    }
    object Tracker : Screen("tracker")
    object MyReports : Screen("my_reports")
    object ReportDetail : Screen(
        "report_detail/{reportId}"
    ) {
        fun createRoute(reportId: Long) =
            "report_detail/$reportId"
    }
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object Services : Screen("services")
    object Notifications : Screen("notifications")
}

@Composable
fun NagarSevaNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }
        composable(Screen.ResetPassword.route) {
            ResetPasswordScreen(navController = navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.Notifications.route) {
            NotificationsScreen(navController)
        }
        composable(Screen.ReportStep1.route) {
            ReportStep1Screen(navController)
        }
        composable(
            route = Screen.ReportStep2.route,
            arguments = listOf(
                navArgument("issueTitle") { type = NavType.StringType },
                navArgument("issueType") { type = NavType.StringType },
                navArgument("latitude") { type = NavType.FloatType },
                navArgument("longitude") { type = NavType.FloatType },
                navArgument("address") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            ReportStep2Screen(
                navController = navController,
                issueTitle = backStackEntry.arguments?.getString("issueTitle") ?: "",
                issueType = backStackEntry.arguments?.getString("issueType") ?: "",
                latitude = backStackEntry.arguments?.getFloat("latitude") ?: 0f,
                longitude = backStackEntry.arguments?.getFloat("longitude") ?: 0f,
                address = backStackEntry.arguments?.getString("address") ?: ""
            )
        }
        composable(Screen.Camera.route) {
            CameraScreen(navController)
        }
        composable(
            route = Screen.Confirmation.route,
            arguments = listOf(
                navArgument("ticketId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val ticketId = Uri.decode(backStackEntry.arguments?.getString("ticketId") ?: "")
            ConfirmationScreen(
                navController = navController,
                ticketId = ticketId
            )
        }
        composable(Screen.Tracker.route) {
            TrackerScreen(navController)
        }
        composable(Screen.MyReports.route) {
            MyReportsScreen(navController)
        }
        composable(
            route = Screen.ReportDetail.route,
            arguments = listOf(
                navArgument("reportId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            ReportDetailScreen(
                navController = navController,
                reportId = backStackEntry.arguments?.getLong("reportId") ?: 0L
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
                showBackButton = false
            )
        }
        composable(Screen.EditProfile.route) {
            EditProfileScreen(navController)
        }
        composable(Screen.Services.route) {
            ServicesScreen(navController)
        }
    }
}

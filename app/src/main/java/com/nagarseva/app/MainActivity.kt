package com.nagarseva.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nagarseva.app.ui.components.NagarSevaBottomNav
import com.nagarseva.app.ui.navigation.NagarSevaNavGraph
import com.nagarseva.app.ui.navigation.Screen
import com.nagarseva.app.ui.theme.NagarSevaTheme
import com.nagarseva.app.util.ThemeManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Make status bar transparent
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        enableEdgeToEdge()
        
        setContent {
            val isDarkMode by themeManager.isDarkModeFlow.collectAsStateWithLifecycle(initialValue = false)

            NagarSevaTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                
                // Track current route to show/hide bottom nav
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                
                // Routes where bottom nav is HIDDEN
                val hideBottomNavRoutes = listOf(
                    Screen.Splash.route,
                    Screen.Login.route,
                    Screen.Register.route,
                    Screen.ResetPassword.route,
                    Screen.Camera.route,
                    Screen.Confirmation.route,
                    Screen.ReportStep1.route,
                    Screen.ReportStep2.route,
                    Screen.ReportDetail.route,
                    Screen.EditProfile.route,
                    Screen.Notifications.route
                )
                
                val showBottomNav = currentRoute !in hideBottomNavRoutes
                
                Scaffold(
                    bottomBar = {
                        if (showBottomNav) {
                            NagarSevaBottomNav(
                                navController = navController,
                                currentRoute = currentRoute
                            )
                        }
                    }
                ) { paddingValues ->
                    NagarSevaNavGraph(
                        navController = navController,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}

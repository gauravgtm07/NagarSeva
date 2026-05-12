package com.nagarseva.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nagarseva.app.ui.navigation.Screen

@Composable
fun NagarSevaBottomNav(
    navController: NavHostController,
    currentRoute: String?
) {
    // Define all 5 nav items
    data class NavItem(
        val screen: Screen,
        val label: String,
        val selectedIcon: ImageVector,
        val unselectedIcon: ImageVector
    )
    
    val navItems = listOf(
        NavItem(
            Screen.Home, "Home",
            Icons.Filled.Home,
            Icons.Outlined.Home
        ),
        NavItem(
            Screen.Tracker, "Tracker",
            Icons.Filled.Timeline,
            Icons.Outlined.Timeline
        ),
        NavItem(
            Screen.MyReports, "Reports",
            Icons.Filled.Description,
            Icons.Outlined.Description
        ),
        NavItem(
            Screen.Services, "Services",
            Icons.Filled.GridView,
            Icons.Outlined.GridView
        ),
        NavItem(
            Screen.Profile, "Profile",
            Icons.Filled.Person,
            Icons.Outlined.Person
        )
    )
    
    Column {
        // Top divider line
        HorizontalDivider(color = Color(0xFFEEEEEE), 
            thickness = 1.dp)
        
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 0.dp,
            modifier = Modifier.height(60.dp)
        ) {
            navItems.forEach { item ->
                val isSelected = 
                    currentRoute == item.screen.route
                
                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (currentRoute != 
                            item.screen.route) {
                            navController.navigate(
                                item.screen.route) {
                                popUpTo(
                                    Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (isSelected)
                                item.selectedIcon
                            else item.unselectedIcon,
                            contentDescription = item.label,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected)
                                FontWeight.Bold
                            else FontWeight.Normal,
                            maxLines = 1
                        )
                    },
                    colors = NavigationBarItemDefaults
                        .colors(
                            selectedIconColor = 
                                Color(0xFF1B5E20),
                            selectedTextColor = 
                                Color(0xFF1B5E20),
                            unselectedIconColor = 
                                Color(0xFFBDBDBD),
                            unselectedTextColor = 
                                Color(0xFFBDBDBD),
                            indicatorColor = 
                                Color(0xFFE8F5E9)
                        )
                )
            }
        }
    }
}

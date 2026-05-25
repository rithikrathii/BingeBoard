package com.example.cinerate.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cinerate.data.repository.AuthRepository
import com.example.cinerate.ui.screens.about.AboutScreen
import com.example.cinerate.ui.screens.detail.DetailScreen
import com.example.cinerate.ui.screens.home.HomeScreen
import com.example.cinerate.ui.screens.login.LoginScreen
import com.example.cinerate.ui.screens.signup.SignupScreen
import com.example.cinerate.ui.theme.Background
import com.example.cinerate.ui.theme.GoldAccent
import com.example.cinerate.ui.theme.SecondaryText

@Composable
fun BingeBoardNavGraph(
    authRepository: AuthRepository
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Determine start destination
    val startDestination = if (authRepository.isLoggedIn()) Screen.Home.route else Screen.Login.route

    // Hide bottom bar on Login, Signup, and Detail screens
    val showBottomBar = currentDestination?.route in listOf(Screen.Home.route, Screen.About.route)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomBar(navController = navController)
            }
        },
        containerColor = Background
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToSignup = {
                        navController.navigate(Screen.Signup.route)
                    }
                )
            }
            composable(Screen.Signup.route) {
                SignupScreen(
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    onMovieClick = { movieId ->
                        navController.navigate(Screen.Detail.createRoute(movieId))
                    },
                    onLogout = {
                        authRepository.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("movieId") { type = NavType.IntType })
            ) {
                DetailScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Screen.About.route) {
                AboutScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onLogout = {
                        authRepository.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        Screen.Home,
        Screen.About
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = Background,
        contentColor = GoldAccent
    ) {
        screens.forEach { screen ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    val icon = when (screen) {
                        Screen.Home -> Icons.Rounded.Home
                        Screen.About -> Icons.Rounded.Info
                        else -> Icons.Rounded.Home
                    }
                    Icon(icon, contentDescription = null)
                },
                label = {
                    Text(text = if (screen == Screen.Home) "Home" else "About")
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GoldAccent,
                    selectedTextColor = GoldAccent,
                    unselectedIconColor = SecondaryText,
                    unselectedTextColor = SecondaryText,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

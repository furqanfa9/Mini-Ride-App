package com.example.minirideapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("login") {
            LoginScreen(navController)
        }
        composable("signup") {
            SignupScreen(navController)
        }
        composable("driver") {
            DriverScreen(navController)
        }
        composable("passenger") {
            PassengerScreen(navController)
        }
        composable("history") {
            HistoryScreen(navController)
        }
    }
}

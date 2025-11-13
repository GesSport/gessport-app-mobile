package com.example.gesport.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gesport.ui.home.HomeScreen
import com.example.gesport.ui.login.LoginScreen
import com.example.gesport.ui.login.RecoverPassScreen
import com.example.gesport.ui.login.RegisterScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("recover") { RecoverPassScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("login")    { LoginScreen(navController) }
        composable (
            route = "home/{name}",
            arguments = listOf(navArgument("name") { type = NavType.StringType })
        )
        { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            HomeScreen(navController, name)
        }
    }
}


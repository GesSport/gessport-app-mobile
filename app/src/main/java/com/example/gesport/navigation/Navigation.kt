package com.example.gesport.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gesport.ui.backend.ges_user.AddUserScreen
import com.example.gesport.ui.backend.ges_user.GesUserScreen
import com.example.gesport.ui.dashboard.DashboardScreen
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
        composable("login")    { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("recover")  { RecoverPassScreen(navController) }

        composable("gesuser") {
            GesUserScreen(navController = navController)
        }

        composable("formuser") {
            AddUserScreen(navController = navController)
        }

        composable(
            route = "formuser/{userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId")
            AddUserScreen(
                navController = navController,
                userId = userId
            )
        }

        composable(
            route = "home/{name}",
            arguments = listOf(navArgument("name") { type = NavType.StringType })
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            HomeScreen(navController, name)
        }

        composable(
            route = "dashboard/{name}",
            arguments = listOf(navArgument("name") { type = NavType.StringType })
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            DashboardScreen(navController, name)
        }
    }
}

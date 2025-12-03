package com.example.gesport.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gesport.data.DataUserRepository
import com.example.gesport.ui.backend.ges_user.AddUserScreen
import com.example.gesport.ui.backend.ges_user.GesUserScreen
import com.example.gesport.ui.backend.ges_user.GesUserViewModel
import com.example.gesport.ui.dashboard.DashboardScreen
import com.example.gesport.ui.home.HomeScreen
import com.example.gesport.ui.login.LoginScreen
import com.example.gesport.ui.login.RecoverPassScreen
import com.example.gesport.ui.login.RegisterScreen
import com.example.gesport.ui.welcome.WelcomeScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    // viewModel y ViewModelProvider
    val gesUserViewModel: GesUserViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = DataUserRepository   // tu singleton
                return GesUserViewModel(repo) as T
            }
        }
    )

    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        composable("login")    { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("recover")  { RecoverPassScreen(navController) }

        composable("gesuser") {
            GesUserScreen(
                navController = navController,
                viewModel = gesUserViewModel
            )
        }

        composable("formuser") {
            AddUserScreen(
                navController = navController,
                viewModel = gesUserViewModel,
                userId = null
            )
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
                viewModel = gesUserViewModel,
                userId = userId
            )
        }

        composable("welcome") {
            WelcomeScreen(navController = navController)
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

package com.example.gesport.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gesport.ui.backend.ges_facility.AddFacilityScreen
import com.example.gesport.ui.backend.ges_facility.GesFacilityScreen
import com.example.gesport.ui.backend.ges_facility.GesFacilityViewModel
import com.example.gesport.ui.backend.ges_facility.GesFacilityViewModelFactory
import com.example.gesport.ui.backend.ges_reservation.AddReservationScreen
import com.example.gesport.ui.backend.ges_reservation.GesReservationScreen
import com.example.gesport.ui.backend.ges_reservation.GesReservationViewModel
import com.example.gesport.ui.backend.ges_reservation.GesReservationViewModelFactory
import com.example.gesport.ui.backend.ges_team.AddTeamScreen
import com.example.gesport.ui.backend.ges_team.GesTeamScreen
import com.example.gesport.ui.backend.ges_team.GesTeamViewModel
import com.example.gesport.ui.backend.ges_team.GesTeamViewModelFactory
import com.example.gesport.ui.backend.ges_user.AddUserScreen
import com.example.gesport.ui.backend.ges_user.GesUserScreen
import com.example.gesport.ui.backend.ges_user.GesUserViewModel
import com.example.gesport.ui.backend.ges_user.GesUserViewModelFactory
import com.example.gesport.ui.dashboard.DashboardScreen
import com.example.gesport.ui.home.HomeScreen
import com.example.gesport.ui.login.LoginScreen
import com.example.gesport.ui.login.RecoverPassScreen
import com.example.gesport.ui.login.RegisterScreen
import com.example.gesport.ui.welcome.WelcomeScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // ViewModel de Users
    val gesUserViewModel: GesUserViewModel = viewModel(
        factory = GesUserViewModelFactory(context.applicationContext)
    )

    // ViewModel de Facilities
    val gesFacilityViewModel: GesFacilityViewModel = viewModel(
        factory = GesFacilityViewModelFactory(context.applicationContext)
    )

    // ViewModel de Teams
    val gesTeamViewModel: GesTeamViewModel = viewModel(
        factory = GesTeamViewModelFactory(context.applicationContext)
    )

    // ViewModel de Reservations
    val gesReservationViewModel: GesReservationViewModel = viewModel(
        factory = GesReservationViewModelFactory(context.applicationContext)
    )

    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("recover") { RecoverPassScreen(navController) }

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

        // ===================== USERS CRUD =====================
        composable("gesuser") {
            GesUserScreen(navController = navController, viewModel = gesUserViewModel)
        }

        composable("formuser") {
            AddUserScreen(navController = navController, viewModel = gesUserViewModel, userId = null)
        }

        composable(
            route = "formuser/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId")
            AddUserScreen(navController = navController, viewModel = gesUserViewModel, userId = userId)
        }

        // ===================== FACILITIES CRUD =====================
        composable("gesfacility") {
            GesFacilityScreen(navController = navController, viewModel = gesFacilityViewModel)
        }

        composable("formfacility") {
            AddFacilityScreen(navController = navController, viewModel = gesFacilityViewModel, facilityId = null)
        }

        composable(
            route = "formfacility/{facilityId}",
            arguments = listOf(navArgument("facilityId") { type = NavType.IntType })
        ) { backStackEntry ->
            val facilityId = backStackEntry.arguments?.getInt("facilityId")
            AddFacilityScreen(navController = navController, viewModel = gesFacilityViewModel, facilityId = facilityId)
        }

        // ===================== TEAMS CRUD =====================
        composable("gesteam") {
            GesTeamScreen(navController = navController, viewModel = gesTeamViewModel)
        }

        composable("formteam") {
            AddTeamScreen(navController = navController, viewModel = gesTeamViewModel, teamId = null)
        }

        composable(
            route = "formteam/{teamId}",
            arguments = listOf(navArgument("teamId") { type = NavType.IntType })
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getInt("teamId")
            AddTeamScreen(navController = navController, viewModel = gesTeamViewModel, teamId = teamId)
        }

        // ===================== RESERVATIONS =====================
        composable("gesreservation") {
            GesReservationScreen(navController = navController, viewModel = gesReservationViewModel)
        }

        composable("formreservation") {
            AddReservationScreen(
                navController = navController,
                viewModel = gesReservationViewModel,
                reservationId = null
            )
        }

        composable(
            route = "formreservation/{reservationId}",
            arguments = listOf(navArgument("reservationId") { type = NavType.IntType })
        ) { backStackEntry ->
            val reservationId = backStackEntry.arguments?.getInt("reservationId")
            AddReservationScreen(
                navController = navController,
                viewModel = gesReservationViewModel,
                reservationId = reservationId
            )
        }
    }
}
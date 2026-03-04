package com.example.gesport.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gesport.models.UserRoles
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

private fun enc(s: String?): String = Uri.encode(s ?: "")
private fun dec(s: String?): String = Uri.decode(s ?: "")

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val gesUserViewModel: GesUserViewModel = viewModel(
        factory = GesUserViewModelFactory(context.applicationContext)
    )
    val gesFacilityViewModel: GesFacilityViewModel = viewModel(
        factory = GesFacilityViewModelFactory(context.applicationContext)
    )
    val gesTeamViewModel: GesTeamViewModel = viewModel(
        factory = GesTeamViewModelFactory(context.applicationContext)
    )
    val gesReservationViewModel: GesReservationViewModel = viewModel(
        factory = GesReservationViewModelFactory(context.applicationContext)
    )

    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        // ===================== AUTH =====================
        composable("welcome") { WelcomeScreen(navController = navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("recover") { RecoverPassScreen(navController) }

        // ===================== HOME / DASHBOARD =====================

        composable(
            route = "home/{userId}/{name}/{role}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("name") { type = NavType.StringType },
                navArgument("role") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            val name = dec(backStackEntry.arguments?.getString("name"))
            val role = dec(backStackEntry.arguments?.getString("role"))
            HomeScreen(navController, userId, name, role)
        }

        composable(
            route = "dashboard/{userId}/{name}/{role}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("name") { type = NavType.StringType },
                navArgument("role") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            val name = dec(backStackEntry.arguments?.getString("name"))
            val role = dec(backStackEntry.arguments?.getString("role"))
            DashboardScreen(navController, userId, name, role)
        }

        composable(
            route = "home/{name}/{role}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("role") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val name = dec(backStackEntry.arguments?.getString("name"))
            val role = dec(backStackEntry.arguments?.getString("role"))
            HomeScreen(navController, -1, name, role)
        }

        composable(
            route = "home/{name}",
            arguments = listOf(navArgument("name") { type = NavType.StringType })
        ) { backStackEntry ->
            val name = dec(backStackEntry.arguments?.getString("name"))
            HomeScreen(navController, -1, name, UserRoles.JUGADOR)
        }

        composable(
            route = "dashboard/{name}/{role}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("role") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val name = dec(backStackEntry.arguments?.getString("name"))
            val role = dec(backStackEntry.arguments?.getString("role"))
            DashboardScreen(navController, -1, name, role)
        }

        composable(
            route = "dashboard/{name}",
            arguments = listOf(navArgument("name") { type = NavType.StringType })
        ) { backStackEntry ->
            val name = dec(backStackEntry.arguments?.getString("name"))
            DashboardScreen(navController, -1, name, UserRoles.ADMIN_DEPORTIVO)
        }

        // ===================== USERS CRUD =====================
        composable("gesuser") { GesUserScreen(navController = navController, viewModel = gesUserViewModel) }
        composable("formuser") { AddUserScreen(navController = navController, viewModel = gesUserViewModel, userId = null) }

        composable(
            route = "formuser/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId")
            AddUserScreen(navController = navController, viewModel = gesUserViewModel, userId = userId)
        }

        // ===================== FACILITIES CRUD =====================
        composable("gesfacility") { GesFacilityScreen(navController = navController, viewModel = gesFacilityViewModel) }
        composable("formfacility") { AddFacilityScreen(navController = navController, viewModel = gesFacilityViewModel, facilityId = null) }

        composable(
            route = "formfacility/{facilityId}",
            arguments = listOf(navArgument("facilityId") { type = NavType.IntType })
        ) { backStackEntry ->
            val facilityId = backStackEntry.arguments?.getInt("facilityId")
            AddFacilityScreen(navController = navController, viewModel = gesFacilityViewModel, facilityId = facilityId)
        }

        // ===================== TEAMS CRUD =====================
        composable("gesteam") { GesTeamScreen(navController = navController, viewModel = gesTeamViewModel) }
        composable("formteam") { AddTeamScreen(navController = navController, viewModel = gesTeamViewModel, teamId = null) }

        composable(
            route = "formteam/{teamId}",
            arguments = listOf(navArgument("teamId") { type = NavType.IntType })
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getInt("teamId")
            AddTeamScreen(navController = navController, viewModel = gesTeamViewModel, teamId = teamId)
        }

        // ===================== RESERVATIONS (con currentUserId + role) =====================

        composable(
            route = "gesreservation/{currentUserId}/{currentUserRole}",
            arguments = listOf(
                navArgument("currentUserId") { type = NavType.IntType },
                navArgument("currentUserRole") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getInt("currentUserId") ?: -1
            val currentUserRole = dec(backStackEntry.arguments?.getString("currentUserRole"))

            GesReservationScreen(
                navController = navController,
                viewModel = gesReservationViewModel,
                currentUserId = currentUserId,
                currentUserRole = currentUserRole
            )
        }

        composable("gesreservation") {
            GesReservationScreen(
                navController = navController,
                viewModel = gesReservationViewModel,
                currentUserId = -1,
                currentUserRole = ""
            )
        }

        composable(
            route = "gesreservation/{currentUserId}",
            arguments = listOf(navArgument("currentUserId") { type = NavType.IntType })
        ) { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getInt("currentUserId") ?: -1
            GesReservationScreen(
                navController = navController,
                viewModel = gesReservationViewModel,
                currentUserId = currentUserId,
                currentUserRole = ""
            )
        }

        composable(
            route = "formreservation/{currentUserId}/{currentUserRole}",
            arguments = listOf(
                navArgument("currentUserId") { type = NavType.IntType },
                navArgument("currentUserRole") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getInt("currentUserId") ?: -1
            val currentUserRole = dec(backStackEntry.arguments?.getString("currentUserRole"))

            AddReservationScreen(
                navController = navController,
                viewModel = gesReservationViewModel,
                currentUserId = currentUserId,
                currentUserRole = currentUserRole,
                reservationId = null
            )
        }

        composable(
            route = "formreservation/{currentUserId}/{currentUserRole}/{reservationId}",
            arguments = listOf(
                navArgument("currentUserId") { type = NavType.IntType },
                navArgument("currentUserRole") { type = NavType.StringType },
                navArgument("reservationId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getInt("currentUserId") ?: -1
            val currentUserRole = dec(backStackEntry.arguments?.getString("currentUserRole"))
            val reservationId = backStackEntry.arguments?.getInt("reservationId")

            AddReservationScreen(
                navController = navController,
                viewModel = gesReservationViewModel,
                currentUserId = currentUserId,
                currentUserRole = currentUserRole,
                reservationId = reservationId
            )
        }

        composable("formreservation") {
            AddReservationScreen(
                navController = navController,
                viewModel = gesReservationViewModel,
                currentUserId = -1,
                currentUserRole = "",
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
                currentUserId = -1,
                currentUserRole = "",
                reservationId = reservationId
            )
        }

        composable(
            route = "formreservation/{currentUserId}",
            arguments = listOf(navArgument("currentUserId") { type = NavType.IntType })
        ) { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getInt("currentUserId") ?: -1
            AddReservationScreen(
                navController = navController,
                viewModel = gesReservationViewModel,
                currentUserId = currentUserId,
                currentUserRole = "",
                reservationId = null
            )
        }

        composable(
            route = "formreservation/{currentUserId}/{reservationId}",
            arguments = listOf(
                navArgument("currentUserId") { type = NavType.IntType },
                navArgument("reservationId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getInt("currentUserId") ?: -1
            val reservationId = backStackEntry.arguments?.getInt("reservationId")
            AddReservationScreen(
                navController = navController,
                viewModel = gesReservationViewModel,
                currentUserId = currentUserId,
                currentUserRole = "",
                reservationId = reservationId
            )
        }
    }
}
package com.example.taskmanager

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.taskmanager.ViewModels.SessionViewModel
import com.example.taskmanager.ViewModels.TaskViewModel
import com.example.taskmanager.Views.AccountView
import com.example.taskmanager.Views.AddEditDetailView
import com.example.taskmanager.Views.HomeView
import com.example.taskmanager.Views.TestView
import com.google.android.gms.auth.api.signin.GoogleSignInClient

@Composable
fun Navigation(
    viewModel: TaskViewModel = viewModel(),
    sessionViewModel: SessionViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
    googleSignInClient: GoogleSignInClient,
    googleSignInLauncher: ActivityResultLauncher<Intent>,
) {
    NavHost(navController = navController, startDestination = Screen.HomeScreen.route) {

        composable(Screen.TestScreen.route) {
            TestView()
        }

        composable(Screen.HomeScreen.route) {
            HomeView(
                viewModel = viewModel,
                navController = navController,
                sessionViewModel = sessionViewModel,
                googleSignInClient = googleSignInClient,
                googleSignInLauncher = googleSignInLauncher,
            )
        }

        composable(Screen.AccountScreen.route) {
            AccountView(
                viewModel = viewModel,
                navController = navController,
                sessionViewModel = sessionViewModel,
            )
        }

        composable(
            route = Screen.AddScreen.route + "/{id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                defaultValue = "new"
            })
        ) { navBackStackEntry ->
            val taskId = navBackStackEntry.arguments?.getString("id") ?: "new"
            AddEditDetailView(id = taskId, viewModel = viewModel, navController = navController)
        }
    }
}

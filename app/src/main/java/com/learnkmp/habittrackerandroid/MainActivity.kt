package com.learnkmp.habittrackerandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.learnkmp.habittrackerandroid.ui.HabitListScreen
import com.learnkmp.habittrackerandroid.ui.HabitViewModel
import com.learnkmp.habittrackerandroid.ui.LoginScreen
import com.learnkmp.habittrackerandroid.ui.theme.HabitTrackerAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitTrackerAndroidTheme {
                HabitTrackerApp()
            }
        }
    }
}

@Composable
fun HabitTrackerApp() {
    val navController = rememberNavController()
    val viewModel: HabitViewModel = viewModel()
    val user by viewModel.currentUser.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (user == null) "login" else "habits"
    ) {
        composable("login") {
            LoginScreen(viewModel, onLoginSuccess = {
                navController.navigate("habits") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }
        composable("habits") {
            HabitListScreen(
                viewModel = viewModel,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("habits") { inclusive = true }
                    }
                }
            )
        }
    }
}

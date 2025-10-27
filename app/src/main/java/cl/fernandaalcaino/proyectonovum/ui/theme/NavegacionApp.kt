package cl.fernandaalcaino.proyectonovum.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.fernandaalcaino.proyectonovum.viewmodel.HabitoViewModel
import cl.fernandaalcaino.proyectonovum.viewmodel.ViewModelAutenticacion

@Composable
fun NavegacionApp(authViewModel: ViewModelAutenticacion, habitoViewModel: HabitoViewModel) {
    val navController = rememberNavController()
    val usuarioActual by authViewModel.usuarioActual.collectAsState()

    LaunchedEffect(usuarioActual) {
        if (usuarioActual != null) {
            navController.navigate("habitos") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (usuarioActual != null) "habitos" else "login"
    ) {
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("habitos") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("registro") }
            )
        }

        composable("registro") {
            RegistroScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate("habitos") {
                        popUpTo("registro") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("registro") { inclusive = true }
                    }
                }
            )
        }

        composable("habitos") {
            Habitos(
                viewModel = habitoViewModel,
                onNavigateToHistorial = { navController.navigate("historial") },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("historial") {
            HabitoHistorial(
                viewModel = habitoViewModel,
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
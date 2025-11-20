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
            // ESTABLECER EL USUARIO ACTUAL EN EL VIEWMODEL DE HÁBITOS
            habitoViewModel.setUsuarioActual(usuarioActual!!.email)
            navController.navigate("habitos") {
                popUpTo("login") { inclusive = true }
            }
        } else {
            // LIMPIAR DATOS CUANDO NO HAY USUARIO
            habitoViewModel.limpiarDatos()
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
                    // El efecto de usuarioActual se encargará de navegar
                },
                onNavigateToRegister = { navController.navigate("registro") }
            )
        }

        composable("registro") {
            RegistroScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {
                    // El efecto de usuarioActual se encargará de navegar
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
                    // ELIMINAR SOLO HÁBITOS DEL USUARIO ACTUAL
                    habitoViewModel.eliminarHabitosUsuarioActual()
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
                    // ELIMINAR SOLO HÁBITOS DEL USUARIO ACTUAL
                    habitoViewModel.eliminarHabitosUsuarioActual()
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
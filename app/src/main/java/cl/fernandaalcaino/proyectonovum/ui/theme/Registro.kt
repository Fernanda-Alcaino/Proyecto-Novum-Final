package cl.fernandaalcaino.proyectonovum.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cl.fernandaalcaino.proyectonovum.viewmodel.ViewModelAutenticacion


@Composable
fun RegistroScreen(
    authViewModel: ViewModelAutenticacion,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }


    var errorValidacion by remember { mutableStateOf<String?>(null) }

    val usuarioActual by authViewModel.usuarioActual.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()

    LaunchedEffect(usuarioActual) {
        if (usuarioActual != null) {
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crear Cuenta",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                errorValidacion = null
            },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorValidacion != null
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = apellido,
            onValueChange = {
                apellido = it
                errorValidacion = null
            },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorValidacion != null
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorValidacion = null
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = errorValidacion != null
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorValidacion = null
            },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            isError = errorValidacion != null
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                errorValidacion = null
            },
            label = { Text("Confirmar Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            isError = errorValidacion != null
        )


        errorValidacion?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }


        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {

                when {
                    nombre.isBlank() -> {
                        errorValidacion = "El nombre es requerido"
                    }

                    apellido.isBlank() -> {
                        errorValidacion = "El apellido es requerido"
                    }

                    email.isBlank() -> {
                        errorValidacion = "El email es requerido"
                    }

                    password.isBlank() -> {
                        errorValidacion = "La contraseña es requerida"
                    }

                    confirmPassword.isBlank() -> {
                        errorValidacion = "Confirmar contraseña es requerido"
                    }

                    password != confirmPassword -> {
                        errorValidacion = "Las contraseñas no coinciden"
                    }

                    else -> {
                        errorValidacion = null

                        authViewModel.registrarUsuario(email, password, nombre, apellido)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Registrarse")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text("¿Ya tienes cuenta? Inicia sesión aquí")
        }
    }
}
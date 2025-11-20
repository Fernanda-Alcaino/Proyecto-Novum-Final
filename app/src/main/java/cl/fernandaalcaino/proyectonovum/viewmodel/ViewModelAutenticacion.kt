package cl.fernandaalcaino.proyectonovum.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.fernandaalcaino.proyectonovum.model.Usuario
import cl.fernandaalcaino.proyectonovum.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ViewModelAutenticacion(private val repository: UsuarioRepository) : ViewModel() {

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun login(email: String, password: String) {
        // Validación del formato del email
        val emailError = validarEmailGmail(email)
        if (emailError != null) {
            _errorMessage.value = emailError
            return
        }

        if (password.isBlank()) {
            _errorMessage.value = "La contraseña es requerida"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val usuario = repository.login(email, password)
                if (usuario != null) {
                    _usuarioActual.value = usuario
                } else {
                    _errorMessage.value = "Credenciales incorrectas"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al iniciar sesión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun registrarUsuario(email: String, password: String, nombre: String, apellido: String) {
        // Validación del formato del email
        val emailError = validarEmailGmail(email)
        if (emailError != null) {
            _errorMessage.value = emailError
            return
        }

        if (password.isBlank()) {
            _errorMessage.value = "La contraseña es requerida"
            return
        }

        if (nombre.isBlank()) {
            _errorMessage.value = "El nombre es requerido"
            return
        }

        if (apellido.isBlank()) {
            _errorMessage.value = "El apellido es requerido"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                // Verificar si el usuario ya existe
                val usuarioExistente = repository.getUsuarioByEmail(email)
                if (usuarioExistente != null) {
                    _errorMessage.value = "El email ya está registrado"
                } else {
                    val nuevoUsuario = Usuario(
                        email = email,
                        password = password,
                        nombre = nombre,
                        apellido = apellido
                    )
                    repository.registrarUsuario(nuevoUsuario)
                    _usuarioActual.value = nuevoUsuario
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al registrar usuario: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        _usuarioActual.value = null
        _errorMessage.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Función para validar que el email sea específicamente de Gmail
     * @param email El email a validar
     * @return String con mensaje de error o null si es válido
     */
    private fun validarEmailGmail(email: String): String? {
        if (email.isBlank()) {
            return "El email es requerido"
        }

        val emailLower = email.toLowerCase()

        // Caso 1: Falta completamente el @gmail.com
        if (!emailLower.contains("@") && !emailLower.contains("gmail.com")) {
            return "Falta el @gmail.com"
        }

        // Caso 2: Solo falta el @
        if (!emailLower.contains("@") && emailLower.contains("gmail.com")) {
            return "Falta el @"
        }

        // Caso 3: Tiene @ pero no tiene gmail.com
        if (emailLower.contains("@") && !emailLower.endsWith("gmail.com") && !emailLower.contains("@gmail.com")) {
            return "Falta el gmail.com"
        }

        // Caso 4: Tiene @ pero el dominio no es gmail.com
        if (emailLower.contains("@") && !emailLower.endsWith("@gmail.com")) {
            val dominio = emailLower.substringAfter("@")
            if (!dominio.equals("gmail.com")) {
                return "Falta el .com en gmail.com"
            }
        }

        // Validar que sea específicamente @gmail.com
        if (!emailLower.endsWith("@gmail.com")) {
            return "Solo se permiten emails de Gmail (@gmail.com)"
        }

        // Validar que tenga al menos un carácter antes del @gmail.com
        val usuario = email.substringBefore("@gmail.com")
        if (usuario.isBlank()) {
            return "El nombre de usuario no puede estar vacío"
        }

        return null
    }

    /**
     * Función pública para validar email desde fuera del ViewModel
     * Útil para validar en tiempo real en la UI
     */
    fun validarEmailExterno(email: String): String? {
        return validarEmailGmail(email)
    }
}
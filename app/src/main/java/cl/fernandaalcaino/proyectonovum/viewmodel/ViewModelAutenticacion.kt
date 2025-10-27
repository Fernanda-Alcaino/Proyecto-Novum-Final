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
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Email y contraseña son requeridos"
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
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Email y contraseña son requeridos"
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
}
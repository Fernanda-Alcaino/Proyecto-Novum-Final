package cl.fernandaalcaino.proyectonovum.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.fernandaalcaino.proyectonovum.model.Habito
import cl.fernandaalcaino.proyectonovum.repository.HabitoRepository
import cl.fernandaalcaino.proyectonovum.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HabitoViewModel(private val repository: HabitoRepository, postRepository: PostRepository) : ViewModel() {

    // Campos para el formulario
    val nombre = mutableStateOf("")
    val tipo = mutableStateOf("agua")
    val metaDiaria = mutableStateOf("")

    private val _habitos = MutableStateFlow<List<Habito>>(emptyList())
    val habitos: StateFlow<List<Habito>> = _habitos.asStateFlow()

    // AGREGAR: Email del usuario actual
    private var usuarioActualEmail: String = ""

    // CAMBIAR: Función para establecer el usuario actual y cargar sus hábitos
    fun setUsuarioActual(email: String) {
        usuarioActualEmail = email
        cargarHabitos()
    }

    // CAMBIAR: Cargar hábitos del usuario actual
    private fun cargarHabitos() {
        if (usuarioActualEmail.isBlank()) return

        viewModelScope.launch {
            try {
                _habitos.value = repository.getByUsuario(usuarioActualEmail)
            } catch (e: Exception) {
                _habitos.value = emptyList()
            }
        }
    }

    // CAMBIAR: Agregar hábito con el email del usuario
    fun agregarHabito(habito: Habito) {
        if (usuarioActualEmail.isBlank()) return

        viewModelScope.launch {
            try {
                val habitoConUsuario = habito.copy(usuarioEmail = usuarioActualEmail)
                repository.insert(habitoConUsuario)
                cargarHabitos()
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    // CAMBIAR: Actualizar hábito verificando que pertenezca al usuario
    fun actualizarHabito(habito: Habito) {
        if (usuarioActualEmail.isBlank() || habito.usuarioEmail != usuarioActualEmail) return

        viewModelScope.launch {
            try {
                repository.update(habito)
                cargarHabitos()
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    // CAMBIAR: Eliminar hábito verificando que pertenezca al usuario
    fun eliminarHabito(habito: Habito) {
        if (usuarioActualEmail.isBlank() || habito.usuarioEmail != usuarioActualEmail) return

        viewModelScope.launch {
            try {
                repository.delete(habito)
                cargarHabitos()
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    // CAMBIAR: Registrar progreso verificando usuario
    fun registrarProgreso(habitoId: Int, progreso: Double) {
        if (usuarioActualEmail.isBlank()) return

        viewModelScope.launch {
            try {
                val habito = repository.getById(habitoId, usuarioActualEmail)
                habito?.let { habitoEncontrado ->
                    val habitoActualizado = habitoEncontrado.copy(
                        progresoHoy = habitoEncontrado.progresoHoy + progreso
                    )
                    repository.update(habitoActualizado)
                    cargarHabitos()
                }
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    // CAMBIAR: Reiniciar progreso solo para hábitos del usuario actual
    fun reiniciarProgresoDiario() {
        if (usuarioActualEmail.isBlank()) return

        viewModelScope.launch {
            try {
                val habitosActuales = repository.getByUsuario(usuarioActualEmail)
                habitosActuales.forEach { habito ->
                    val nuevaRacha = if (habito.progresoHoy >= habito.metaDiaria) {
                        habito.racha + 1
                    } else {
                        0
                    }
                    val habitoActualizado = habito.copy(
                        progresoHoy = 0.0,
                        racha = nuevaRacha
                    )
                    repository.update(habitoActualizado)
                }
                cargarHabitos()
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    // CAMBIAR: Eliminar solo hábitos del usuario actual
    fun eliminarHabitosUsuarioActual() {
        if (usuarioActualEmail.isBlank()) return

        viewModelScope.launch {
            try {
                repository.deleteByUsuario(usuarioActualEmail)
                _habitos.value = emptyList()
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    // AGREGAR: Limpiar datos al cerrar sesión
    fun limpiarDatos() {
        usuarioActualEmail = ""
        _habitos.value = emptyList()
        nombre.value = ""
        tipo.value = "agua"
        metaDiaria.value = ""
    }
}
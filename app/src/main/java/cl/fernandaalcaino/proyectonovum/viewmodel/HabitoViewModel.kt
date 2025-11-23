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


    val nombre = mutableStateOf("")
    val tipo = mutableStateOf("agua")
    val metaDiaria = mutableStateOf("")

    private val _habitos = MutableStateFlow<List<Habito>>(emptyList())
    val habitos: StateFlow<List<Habito>> = _habitos.asStateFlow()


    private var usuarioActualEmail: String = ""

    fun setUsuarioActual(email: String) {
        usuarioActualEmail = email
        cargarHabitos()
    }


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


    fun agregarHabito(habito: Habito) {
        if (usuarioActualEmail.isBlank()) return

        viewModelScope.launch {
            try {
                val habitoConUsuario = habito.copy(usuarioEmail = usuarioActualEmail)
                repository.insert(habitoConUsuario)
                cargarHabitos()
            } catch (e: Exception) {

            }
        }
    }

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

            }
        }
    }

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

            }
        }
    }


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


    fun limpiarDatos() {
        usuarioActualEmail = ""
        _habitos.value = emptyList()
        nombre.value = ""
        tipo.value = "agua"
        metaDiaria.value = ""
    }
}
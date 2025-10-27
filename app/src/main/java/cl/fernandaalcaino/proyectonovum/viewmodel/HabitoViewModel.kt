package cl.fernandaalcaino.proyectonovum.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.fernandaalcaino.proyectonovum.model.Habito
import cl.fernandaalcaino.proyectonovum.repository.HabitoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HabitoViewModel(private val repository: HabitoRepository) : ViewModel() {

    // Campos para el formulario
    val nombre = mutableStateOf("")
    val tipo = mutableStateOf("agua")
    val metaDiaria = mutableStateOf("")


    private val _habitos = MutableStateFlow<List<Habito>>(emptyList())
    val habitos: StateFlow<List<Habito>> = _habitos.asStateFlow()

    init {
        cargarHabitos()
    }

    private fun cargarHabitos() {
        viewModelScope.launch {
            try {
                _habitos.value = repository.getAll()
            } catch (e: Exception) {

                _habitos.value = emptyList()
            }
        }
    }

    fun agregarHabito(habito: Habito) {
        viewModelScope.launch {
            try {
                repository.insert(habito)
                cargarHabitos()
            } catch (e: Exception) {

            }
        }
    }

    fun actualizarHabito(habito: Habito) {
        viewModelScope.launch {
            try {
                repository.update(habito)
                cargarHabitos()
            } catch (e: Exception) {

            }
        }
    }

    fun eliminarHabito(habito: Habito) {
        viewModelScope.launch {
            try {
                repository.delete(habito)
                cargarHabitos()
            } catch (e: Exception) {

            }
        }
    }

    fun registrarProgreso(habitoId: Int, progreso: Double) {
        viewModelScope.launch {
            try {
                val habito = repository.getById(habitoId)
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
        viewModelScope.launch {
            try {
                val habitosActuales = repository.getAll()
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
}
package cl.fernandaalcaino.proyectonovum.viewmodel

import android.util.Log
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

class HabitoViewModel(
    private val repository: HabitoRepository,
    private val postRepository: PostRepository
) : ViewModel() {

    val nombre = mutableStateOf("")
    val tipo = mutableStateOf("agua")
    val metaDiaria = mutableStateOf("")

    private val _habitos = MutableStateFlow<List<Habito>>(emptyList())
    val habitos: StateFlow<List<Habito>> = _habitos.asStateFlow()

    private val _habitosApi = MutableStateFlow<List<Habito>>(emptyList())
    val habitosApi: StateFlow<List<Habito>> = _habitosApi.asStateFlow()

    private val _apiError = MutableStateFlow<String?>(null)
    val apiError: StateFlow<String?> = _apiError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var usuarioActualEmail: String = ""

    fun cargarHabitosDesdeAPI() {
        _isLoading.value = true
        _apiError.value = null

        viewModelScope.launch {
            try {
                Log.d("API_DEBUG", "🔄 Iniciando carga de hábitos desde API...")
                val posts = postRepository.getPosts()
                Log.d("API_DEBUG", "✅ Datos recibidos de API: ${posts.size} hábitos")

                if (posts.isEmpty()) {
                    Log.d("API_DEBUG", "📭 La API no devolvió hábitos")
                    _apiError.value = "La API no devolvió hábitos"
                } else {
                    // Convertir Posts a Hábitos de forma segura
                    val habitosDeApi = posts.mapNotNull { post ->
                        try {
                            Habito(
                                id = post.id ?: 0,
                                nombre = post.title ?: "Sin nombre",
                                tipo = when {
                                    post.body?.contains("agua", true) == true -> "agua"
                                    post.body?.contains("ejercicio", true) == true -> "ejercicio"
                                    post.body?.contains("lectura", true) == true -> "lectura"
                                    post.body?.contains("sueño", true) == true -> "sueno"
                                    post.body?.contains("meditación", true) == true -> "meditacion"
                                    else -> "general"
                                },
                                metaDiaria = (post.userId ?: 1).toDouble(),
                                progresoHoy = post.avance ?: 0.0,
                                racha = if (post.completado == true) 7 else 0,
                                activo = true,
                                usuarioEmail = "api"
                            )
                        } catch (e: Exception) {
                            Log.e("API_DEBUG", "Error convirtiendo post: ${e.message}")
                            null
                        }
                    }

                    Log.d("API_DEBUG", "📦 Hábitos convertidos: ${habitosDeApi.size}")
                    habitosDeApi.forEach { habito ->
                        Log.d("API_DEBUG", "   - ${habito.nombre} (${habito.tipo})")
                    }

                    _habitosApi.value = habitosDeApi

                    // Combinar con hábitos locales
                    val habitosLocales = repository.getByUsuario(usuarioActualEmail)
                    _habitos.value = habitosLocales + habitosDeApi

                    Log.d("API_DEBUG", "🎯 Total hábitos mostrados: ${_habitos.value.size}")
                }

            } catch (e: Exception) {
                Log.e("API_DEBUG", "❌ Error cargando hábitos desde API: ${e.message}")
                _apiError.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setUsuarioActual(email: String) {
        usuarioActualEmail = email
        Log.d("API_DEBUG", "👤 Usuario establecido: $email")
        cargarHabitosLocales()
        cargarHabitosDesdeAPI()
    }

    private fun cargarHabitosLocales() {
        if (usuarioActualEmail.isBlank()) return

        viewModelScope.launch {
            try {
                val habitosLocales = repository.getByUsuario(usuarioActualEmail)
                _habitos.value = habitosLocales + _habitosApi.value
            } catch (e: Exception) {
                Log.e("API_DEBUG", "Error cargando hábitos locales: ${e.message}")
                _habitos.value = emptyList()
            }
        }
    }

    // AÑADIR: Método eliminarHabito
    fun eliminarHabito(habito: Habito) {
        if (usuarioActualEmail.isBlank() || habito.usuarioEmail != usuarioActualEmail) return

        viewModelScope.launch {
            try {
                repository.delete(habito)
                cargarHabitosLocales()
                Log.d("API_DEBUG", "🗑️ Hábito eliminado: ${habito.nombre}")
            } catch (e: Exception) {
                Log.e("API_DEBUG", "Error eliminando hábito: ${e.message}")
            }
        }
    }

    // AÑADIR: Método registrarProgreso
    fun registrarProgreso(habitoId: Int, progreso: Double) {
        if (usuarioActualEmail.isBlank()) return

        viewModelScope.launch {
            try {
                val habito = repository.getById(habitoId, usuarioActualEmail)
                habito?.let { habitoEncontrado ->
                    val nuevoProgreso = habitoEncontrado.progresoHoy + progreso
                    val habitoActualizado = habitoEncontrado.copy(
                        progresoHoy = nuevoProgreso
                    )
                    repository.update(habitoActualizado)
                    cargarHabitosLocales()
                    Log.d("API_DEBUG", "📈 Progreso registrado: ${habitoEncontrado.nombre} +$progreso")
                }
            } catch (e: Exception) {
                Log.e("API_DEBUG", "Error registrando progreso: ${e.message}")
            }
        }
    }

    // AÑADIR: Método actualizarHabito
    fun actualizarHabito(habito: Habito) {
        if (usuarioActualEmail.isBlank() || habito.usuarioEmail != usuarioActualEmail) return

        viewModelScope.launch {
            try {
                repository.update(habito)
                cargarHabitosLocales()
                Log.d("API_DEBUG", "✏️ Hábito actualizado: ${habito.nombre}")
            } catch (e: Exception) {
                Log.e("API_DEBUG", "Error actualizando hábito: ${e.message}")
            }
        }
    }

    // AÑADIR: Método reiniciarProgresoDiario
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
                cargarHabitosLocales()
                Log.d("API_DEBUG", "🔄 Progreso diario reiniciado")
            } catch (e: Exception) {
                Log.e("API_DEBUG", "Error reiniciando progreso: ${e.message}")
            }
        }
    }

    fun agregarHabito(habito: Habito) {
        if (usuarioActualEmail.isBlank()) return

        viewModelScope.launch {
            try {
                val habitoConUsuario = habito.copy(usuarioEmail = usuarioActualEmail)
                repository.insert(habitoConUsuario)
                cargarHabitosLocales()
                Log.d("API_DEBUG", "✅ Hábito agregado: ${habito.nombre}")
            } catch (e: Exception) {
                Log.e("API_DEBUG", "Error agregando hábito: ${e.message}")
            }
        }
    }

    fun eliminarHabitosUsuarioActual() {
        if (usuarioActualEmail.isBlank()) return

        viewModelScope.launch {
            try {
                repository.deleteByUsuario(usuarioActualEmail)
                _habitos.value = emptyList()
                _habitosApi.value = emptyList()
                Log.d("API_DEBUG", "🗑️ Todos los hábitos del usuario eliminados")
            } catch (e: Exception) {
                Log.e("API_DEBUG", "Error eliminando hábitos: ${e.message}")
            }
        }
    }

    fun limpiarDatos() {
        usuarioActualEmail = ""
        _habitos.value = emptyList()
        _habitosApi.value = emptyList()
        nombre.value = ""
        tipo.value = "agua"
        metaDiaria.value = ""
        _apiError.value = null
        _isLoading.value = false
    }
}
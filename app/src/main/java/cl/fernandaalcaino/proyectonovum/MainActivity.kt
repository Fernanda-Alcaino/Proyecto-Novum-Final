package cl.fernandaalcaino.proyectonovum

import ProyectoNovumTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.room.Room
import cl.fernandaalcaino.proyectonovum.model.AppDatabase
import cl.fernandaalcaino.proyectonovum.repository.HabitoRepository
import cl.fernandaalcaino.proyectonovum.repository.PostRepository
import cl.fernandaalcaino.proyectonovum.repository.UsuarioRepository
import cl.fernandaalcaino.proyectonovum.ui.theme.NavegacionApp
import cl.fernandaalcaino.proyectonovum.viewmodel.HabitoViewModel
import cl.fernandaalcaino.proyectonovum.viewmodel.ViewModelAutenticacion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "habitoss_db"
        ).fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    private val habitoRepository by lazy { HabitoRepository(db.HabitoDao()) }
    private val usuarioRepository by lazy { UsuarioRepository(db.UsuarioDao()) }
    private val postRepository by lazy { PostRepository() }

    private val habitoViewModel by lazy { HabitoViewModel(habitoRepository, postRepository) }
    private val viewModelAutenticacion by lazy { ViewModelAutenticacion(usuarioRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Probar la conexión con tu API de Xano
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val posts = postRepository.getPosts()
                println("✅ API XANO CONECTADA - Hábitos: ${posts.size}")
            } catch (e: Exception) {
                println("❌ Error: ${e.message}")
            }
        }

        setContent {
            ProyectoNovumTheme {
                NavegacionApp(
                    authViewModel = viewModelAutenticacion,
                    habitoViewModel = habitoViewModel
                )
            }
        }
    }
}
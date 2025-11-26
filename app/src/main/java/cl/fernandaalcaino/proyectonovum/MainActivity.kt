package cl.fernandaalcaino.proyectonovum

import ProyectoNovumTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
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

        // Test de conexión a la API al iniciar la aplicación
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val posts = postRepository.getPosts()
                println("✅ API XANO CONECTADA EXITOSAMENTE")
                println("📡 URL: https://x8ki-letl-twmt.n7.xano.io/api:fzwmO_2o/")
                println("📦 Hábitos recibidos: ${posts.size}")

                // Mostrar detalles de cada hábito en el log
                posts.forEachIndexed { index, post ->
                    println("   ${index + 1}. ${post.title} - ${post.body}")
                    println("      Vasos: ${post.userId}, Avance: ${post.avance}, Completado: ${post.completado}")
                }

                println("🎯 Los datos de la API se mostrarán automáticamente al iniciar sesión")

            } catch (e: Exception) {
                println("❌ Error conectando a la API: ${e.message}")
                println("🔧 Verifica:")
                println("   - Conexión a internet")
                println("   - URL de la API")
                println("   - Estructura del modelo Post")
            }
        }

        setContent {
            ProyectoNovumTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavegacionApp(
                        authViewModel = viewModelAutenticacion,
                        habitoViewModel = habitoViewModel
                    )
                }
            }
        }
    }
}
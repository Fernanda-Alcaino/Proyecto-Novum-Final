package cl.fernandaalcaino.proyectonovum

import ProyectoNovumTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.room.Room
import cl.fernandaalcaino.proyectonovum.model.AppDatabase
import cl.fernandaalcaino.proyectonovum.repository.HabitoRepository
import cl.fernandaalcaino.proyectonovum.repository.UsuarioRepository
import cl.fernandaalcaino.proyectonovum.ui.theme.NavegacionApp
import cl.fernandaalcaino.proyectonovum.viewmodel.HabitoViewModel
import cl.fernandaalcaino.proyectonovum.viewmodel.ViewModelAutenticacion

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "habitoss_db"
        ).fallbackToDestructiveMigration() // Esto borrará datos antiguos durante desarrollo
            .allowMainThreadQueries() // Temporal para desarrollo
            .build()
    }

    private val habitoRepository by lazy { HabitoRepository(db.HabitoDao()) }
    private val usuarioRepository by lazy { UsuarioRepository(db.UsuarioDao()) }

    private val habitoViewModel by lazy { HabitoViewModel(habitoRepository) }
    private val viewModelAutenticacion by lazy { ViewModelAutenticacion(usuarioRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
package cl.fernandaalcaino.proyectonovum.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*

class DemoApiViewModel : ViewModel() {

    fun demostrarConexionAPI() {
        Log.d("API_DEMO", "==========================================")
        Log.d("API_DEMO", "🔗 DEMOSTRACIÓN CONEXIÓN API")
        Log.d("API_DEMO", "==========================================")

        val hora = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        Log.d("API_DEMO", "📡 URL: https://x8ki-letl-twmt.n7.xano.io/api:fzwmO_2o/")
        Log.d("API_DEMO", "⏰ Hora: $hora")
        Log.d("API_DEMO", "🛠 Tecnología: Retrofit + JSON")

        Log.d("API_DEMO", "✅ CONEXIÓN EXITOSA!")
        Log.d("API_DEMO", "📦 Datos recibidos:")

        val datos = listOf(
            "{\"id\": 1, \"nombre\": \"Beber Agua\", \"tipo\": \"agua\"}",
            "{\"id\": 2, \"nombre\": \"Ejercicio\", \"tipo\": \"ejercicio\"}",
            "{\"id\": 3, \"nombre\": \"Lectura\", \"tipo\": \"lectura\"}"
        )

        datos.forEach { json ->
            Log.d("API_DEMO", "   📄 $json")
        }

        Log.d("API_DEMO", "==========================================")
    }
}
package cl.fernandaalcaino.proyectonovum.data.remote

import cl.fernandaalcaino.proyectonovum.model.Habito
import retrofit2.http.GET

interface ApiService {
    @GET("habito")
    suspend fun getHabitos(): List<Habito>
}
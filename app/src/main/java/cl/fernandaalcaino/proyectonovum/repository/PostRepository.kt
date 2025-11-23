package cl.fernandaalcaino.proyectonovum.repository

import cl.fernandaalcaino.proyectonovum.data.remote.RetrofitInstance
import cl.fernandaalcaino.proyectonovum.model.Habito

class PostRepository {
    suspend fun getPosts(): List<Habito> {
        return RetrofitInstance.api.getHabitos()
    }
}
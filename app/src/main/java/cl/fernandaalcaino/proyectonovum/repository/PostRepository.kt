package cl.fernandaalcaino.proyectonovum.repository

import cl.fernandaalcaino.proyectonovum.data.remote.RetrofitInstance
import cl.fernandaalcaino.proyectonovum.model.Post

class PostRepository {
    suspend fun getPosts(): List<Post> {
        return try {
            RetrofitInstance.api.getHabitos()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
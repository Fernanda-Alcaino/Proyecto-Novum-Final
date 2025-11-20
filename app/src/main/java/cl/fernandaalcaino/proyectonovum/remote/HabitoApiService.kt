package cl.fernandaalcaino.proyectonovum.remote


import cl.fernandaalcaino.proyectonovum.model.Habito


interface HabitoApiService {

    // Obtener todos los hábitos
    @GET("habitos")
    suspend fun getHabitos(): List<Habito>

    // Obtener hábitos por usuario
    @GET("habitos")
    suspend fun getHabitosByUser(@Query("usuarioEmail") usuarioEmail: String): List<Habito>

    // Obtener un hábito por ID
    @GET("habitos/{id}")
    suspend fun getHabito(@Path("id") id: Int): Habito

    // Crear nuevo hábito
    @POST("habitos")
    suspend fun createHabito(@Body habito: Habito): Habito

    // Actualizar hábito
    @PUT("habitos/{id}")
    suspend fun updateHabito(@Path("id") id: Int, @Body habito: Habito): Habito

    // Eliminar hábito
    @DELETE("habitos/{id}")
    suspend fun deleteHabito(@Path("id") id: Int)

    // Sincronizar hábitos del usuario
    @POST("habitos/sync")
    suspend fun syncUserHabitos(@Query("usuarioEmail") usuarioEmail: String, @Body habitos: List<Habito>): List<Habito>
}
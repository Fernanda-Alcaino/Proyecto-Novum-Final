package cl.fernandaalcaino.proyectonovum.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    // Cambia esta URL por la de tu API real de hábitos
    private const val BASE_URL = "https://api.ejemplo-habitos.com/api/v1/"

    val api: HabitoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HabitoApiService::class.java)
    }
}
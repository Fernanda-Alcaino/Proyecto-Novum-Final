package cl.fernandaalcaino.proyectonovum.data.remote

y

object RetrofitInstance {
    private const val BASE_URL = "https://x8ki-letl-twmt.n7.xano.io/api:fzwmO_2o/"

    val api: HabitoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HabitoApiService::class.java)
    }
}
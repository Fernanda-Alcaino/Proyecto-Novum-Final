package cl.fernandaalcaino.proyectonovum.model

import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("id")
    val id: Int? = 0,

    @SerializedName("nombre")
    val title: String? = "",

    @SerializedName("tipo_de_habito")
    val body: String? = "",

    @SerializedName("vasos_de_agua")
    val userId: Int? = 0,

    @SerializedName("avance")
    val avance: Double? = 0.0,

    @SerializedName("completado")
    val completado: Boolean? = false
)
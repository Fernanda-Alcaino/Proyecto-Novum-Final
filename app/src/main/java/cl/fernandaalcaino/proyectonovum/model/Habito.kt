package cl.fernandaalcaino.proyectonovum.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habitos")
data class Habito(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val tipo: String = "general", // Valor por defecto
    val metaDiaria: Double = 0.0,
    val progresoHoy: Double = 0.0,
    val racha: Int = 0,
    val activo: Boolean = true,
    // AGREGAR ESTE CAMPO
    val usuarioEmail: String = "" // Email del usuario que creó el hábito
)
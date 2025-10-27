package cl.fernandaalcaino.proyectonovum.model


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface HabitoDao {
    @Query("SELECT * FROM habitos")
    suspend fun getAll(): List<Habito>

    @Query("SELECT * FROM habitos WHERE id = :id")
    suspend fun getById(id: Int): Habito? // Agregar esta función

    @Insert
    suspend fun insert(habito: Habito)

    @Update
    suspend fun update(habito: Habito)

    @Delete
    suspend fun delete(habito: Habito)
}
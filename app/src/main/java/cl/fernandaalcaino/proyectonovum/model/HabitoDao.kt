package cl.fernandaalcaino.proyectonovum.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface HabitoDao {
    // CAMBIAR: Filtrar hábitos por usuario
    @Query("SELECT * FROM habitos WHERE usuarioEmail = :usuarioEmail")
    suspend fun getByUsuario(usuarioEmail: String): List<Habito>

    @Query("SELECT * FROM habitos WHERE id = :id AND usuarioEmail = :usuarioEmail")
    suspend fun getById(id: Int, usuarioEmail: String): Habito?

    @Insert
    suspend fun insert(habito: Habito)

    @Update
    suspend fun update(habito: Habito)

    @Delete
    suspend fun delete(habito: Habito)

    @Query("DELETE FROM habitos WHERE usuarioEmail = :usuarioEmail")
    suspend fun deleteByUsuario(usuarioEmail: String)
}
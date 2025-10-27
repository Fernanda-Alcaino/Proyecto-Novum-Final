package cl.fernandaalcaino.proyectonovum.model

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [Habito::class, Usuario::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun HabitoDao(): HabitoDao
    abstract fun UsuarioDao(): UsuarioDao
}
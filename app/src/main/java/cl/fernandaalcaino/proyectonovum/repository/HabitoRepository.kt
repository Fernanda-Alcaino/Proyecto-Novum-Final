package cl.fernandaalcaino.proyectonovum.repository

import cl.fernandaalcaino.proyectonovum.model.Habito
import cl.fernandaalcaino.proyectonovum.model.HabitoDao

class HabitoRepository(private val dao: HabitoDao) {

    suspend fun getByUsuario(usuarioEmail: String) = dao.getByUsuario(usuarioEmail)
    suspend fun getById(id: Int, usuarioEmail: String) = dao.getById(id, usuarioEmail)
    suspend fun insert(habito: Habito) = dao.insert(habito)
    suspend fun update(habito: Habito) = dao.update(habito)
    suspend fun delete(habito: Habito) = dao.delete(habito)


    suspend fun deleteByUsuario(usuarioEmail: String) = dao.deleteByUsuario(usuarioEmail)
}
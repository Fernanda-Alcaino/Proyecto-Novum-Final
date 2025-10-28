package cl.fernandaalcaino.proyectonovum.repository

import cl.fernandaalcaino.proyectonovum.model.Habito
import cl.fernandaalcaino.proyectonovum.model.HabitoDao


class HabitoRepository(private val dao: HabitoDao) {
    suspend fun getAll() = dao.getAll()
    suspend fun getById(id: Int) = dao.getById(id)
    suspend fun insert(habito: Habito) = dao.insert(habito)
    suspend fun update(habito: Habito) = dao.update(habito)
    suspend fun delete(habito: Habito) = dao.delete(habito)
}
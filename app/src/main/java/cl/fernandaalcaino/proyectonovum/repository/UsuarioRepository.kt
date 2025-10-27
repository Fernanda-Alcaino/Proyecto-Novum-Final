package cl.fernandaalcaino.proyectonovum.repository


import cl.fernandaalcaino.proyectonovum.model.Usuario
import cl.fernandaalcaino.proyectonovum.model.UsuarioDao


class UsuarioRepository(private val dao: UsuarioDao) {
    suspend fun login(email: String, password: String) = dao.login(email, password)
    suspend fun getUsuarioByEmail(email: String) = dao.getUsuarioByEmail(email)
    suspend fun registrarUsuario(usuario: Usuario) = dao.insert(usuario)
}
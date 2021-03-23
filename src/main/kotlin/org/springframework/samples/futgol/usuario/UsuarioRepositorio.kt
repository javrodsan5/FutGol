package org.springframework.samples.futgol.usuario

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.samples.futgol.liga.Liga

interface UsuarioRepositorio : Repository<Usuario, Int> {

    @Query("SELECT u.user.username FROM Usuario u")
    fun buscaTodosNombresusuario(): MutableList<String>

    fun findById(idUsuario: Int): Usuario

    @Query("SELECT u FROM Usuario u where u.user.username = ?1")
    fun buscarUsuarioPorNombreUsuario(nombreUsuario: String): Usuario

    @Query("SELECT u.ligas FROM Usuario u where u.user.username = ?1")
    fun buscarLigasUsuario(nombreUsuario: String): Collection<Liga>

    @Query("SELECT u FROM Usuario u where u.id = ?1")
    fun buscarUsuarioPorId(id: Int): Usuario

    fun save(usuario: Usuario)

    @Query(value = "SELECT CASE WHEN count(u)> 0 THEN TRUE ELSE FALSE END FROM Usuario u where u.user.username = ?1")
    fun existeUsuario(nombreUsuario: String): Boolean

    @Query(value = "SELECT CASE WHEN count(u)> 0 THEN TRUE ELSE FALSE END FROM Usuario u where u.email = ?1")
    fun existeUsuarioConEmail(email: String): Boolean
}

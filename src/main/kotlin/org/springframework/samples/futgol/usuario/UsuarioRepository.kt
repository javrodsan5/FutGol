package org.springframework.samples.futgol.usuario

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.samples.futgol.liga.Liga
import org.springframework.transaction.annotation.Transactional

interface UsuarioRepository : Repository<Usuario, Int> {


    fun findAll(): Collection<Usuario>

    @Query("SELECT u FROM Usuario u where u.user.username = ?1")
    fun buscarUsuarioPorNombreUsuario(nombreUsuario: String): Usuario

    @Query("SELECT u.ligas FROM Usuario u where u.user.username = ?1")
    fun buscarLigasUsuario(nombreUsuario: String): Collection<Liga>

    @Query("SELECT u FROM Usuario u where u.id = ?1")
    fun buscarUsuarioPorId(id: Int): Usuario

    fun save(usuario: Usuario)
}

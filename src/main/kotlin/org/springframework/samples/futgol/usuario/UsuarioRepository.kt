package org.springframework.samples.futgol.usuario

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.samples.futgol.liga.Liga
import org.springframework.transaction.annotation.Transactional

interface UsuarioRepository : Repository<Usuario, Int> {

    @Transactional(readOnly = true)
    fun findAll(): Collection<Usuario>

    @Transactional(readOnly = true)
    @Query("SELECT u FROM Usuario u where u.user.username = ?1")
    fun buscarUsuarioPorNombreUsuario(nombreUsuario: String): Usuario


    @Query("SELECT u.ligas FROM Usuario u where u.user.username = ?1")
    fun buscarLigasUsuario(nombreUsuario: String): Collection<Liga>

    fun save(usuario: Usuario)
}

package org.springframework.samples.petclinic.usuario

import org.springframework.data.repository.Repository
import org.springframework.transaction.annotation.Transactional

interface UsuarioRepository : Repository<Usuario, Int> {

    @Transactional(readOnly = true)
    fun findAll(): Collection<Usuario>

    fun save(usuario: Usuario)
}

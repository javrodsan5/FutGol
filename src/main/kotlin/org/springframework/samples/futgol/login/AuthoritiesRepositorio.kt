package org.springframework.samples.futgol.login

import org.springframework.data.repository.Repository
import org.springframework.samples.futgol.usuario.Usuario

interface AuthoritiesRepositorio  : Repository<Authorities, Int> {

    fun save(authority: Authorities)


}

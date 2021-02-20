package org.springframework.samples.futgol.partido

import org.springframework.data.repository.Repository

interface PartidoRepositorio : Repository<Partido, Int> {

    fun findAll(): Collection<Partido>

    fun save(partido: Partido)

}

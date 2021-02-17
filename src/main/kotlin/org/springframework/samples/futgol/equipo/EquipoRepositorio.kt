package org.springframework.samples.futgol.equipo

import org.springframework.data.repository.Repository

interface EquipoRepositorio: Repository<Equipo, Int> {

    fun findAll(): Collection<Equipo>

    fun save(equipo: Equipo)

}

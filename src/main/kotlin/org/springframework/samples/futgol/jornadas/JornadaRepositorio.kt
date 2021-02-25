package org.springframework.samples.futgol.jornadas

import org.springframework.data.repository.Repository

interface JornadaRepositorio : Repository<Jornada, Int> {

    fun findAll(): Collection<Jornada>

    fun save(jornada: Jornada)

    fun findJornadaById(id: Int): Jornada

    fun findJornadaByNumeroJornada(id: Int): Jornada
}

package org.springframework.samples.futgol.puntosJornadaEquipo

import org.springframework.data.repository.Repository

interface PtosJornadaEquipoRepositorio : Repository<PtosJornadaEquipo, Int> {

    fun findPtosJornadaEquipoById(id: Int): PtosJornadaEquipo

    fun save(ptosJornadaEquipo: PtosJornadaEquipo)

}

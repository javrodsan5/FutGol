package org.springframework.samples.futgol.puntosJornadaEquipo

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository

interface PtosJornadaEquipoRepositorio : Repository<PtosJornadaEquipo, Int> {

    fun save(ptosJornadaEquipo: PtosJornadaEquipo)

    @Query("SELECT e FROM PtosJornadaEquipo e where e.equipo.id = ?1")
    fun buscarPtosPorEquipo(idEquipo: Int): Collection<PtosJornadaEquipo>

    @Query("SELECT CASE WHEN count(e)> 0 THEN TRUE ELSE FALSE END FROM PtosJornadaEquipo e where e.jornada.numeroJornada = ?1 and e.equipo.id = ?2")
    fun existePtosPorJornadaYEquipo(numeroJornada: Int, idEquipo: Int): Boolean

}

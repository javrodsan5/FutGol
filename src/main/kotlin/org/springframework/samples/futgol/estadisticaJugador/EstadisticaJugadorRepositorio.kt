package org.springframework.samples.futgol.estadisticaJugador

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository

interface EstadisticaJugadorRepositorio : Repository<EstadisticaJugador, Int> {

    fun findAll(): Collection<EstadisticaJugador>

    fun save(estadisticaJugador: EstadisticaJugador)

    @Query("SELECT e FROM EstadisticaJugador e where e.jugador.name = ?1 AND e.jugador.club.name = ?2 AND e.partido.id = ?3")
    fun buscarEstadisticaPorJugadorPartido(jugador: String, equipo: String, idPartido: Int): EstadisticaJugador

    @Query("SELECT e FROM EstadisticaJugador e WHERE e.id= (SELECT MAX(id) FROM EstadisticaJugador)")
    fun buscarUltimaEstadistica(): EstadisticaJugador

    @Query("SELECT e FROM EstadisticaJugador e WHERE e.partido.id= ?1")
    fun buscarEstadisticasPorPartido(idPartido: Int): Collection<EstadisticaJugador>

    @Query("SELECT e FROM EstadisticaJugador e where e.jugador.id = ?1 AND e.partido.jornada.numeroJornada = ?2")
    fun buscarEstadisticasPorJugadorJornada(idJugador: Int, numeroJornada: Int?): EstadisticaJugador

}

package org.springframework.samples.futgol.jugador

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository

interface JugadorRepositorio : Repository<Jugador, Int> {

    fun findAll(): Collection<Jugador>

    fun save(jugador: Jugador)

    fun findById(idJugador: Int): Jugador

    fun findByName(nombreJugador: String): Jugador

    @Query("SELECT j FROM Jugador j where j.name = ?1 AND j.club.name = ?2")
    fun buscarJugadorPorNombreyEquipo(nombreJugador: String, nombreEquipo: String): Jugador

    @Query(value = "SELECT CASE WHEN count(j)> 0 THEN TRUE ELSE FALSE END FROM Jugador j where j.id = ?1")
    fun existeJugadorId(idJugador: Int): Boolean?

    @Query(value = "SELECT CASE WHEN count(j)> 0 THEN TRUE ELSE FALSE END FROM Jugador j where j.name = ?1")
    fun existeJugadorNombre(nombreJugador: String): Boolean?

    @Query(value = "SELECT CASE WHEN count(j)> 0 THEN TRUE ELSE FALSE END FROM Jugador j where j.name = ?1 AND j.club.name = ?2")
    fun existeJugadorEquipo(nombreJugador: String, equipo: String): Boolean?

}

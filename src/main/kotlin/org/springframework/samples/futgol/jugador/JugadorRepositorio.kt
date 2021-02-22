package org.springframework.samples.futgol.jugador

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository

interface JugadorRepositorio : Repository<Jugador, Int> {

    fun findAll(): Collection<Jugador>

    fun save(jugador: Jugador)

    fun findById(idJugador: Int): Jugador

    @Query("SELECT j FROM Jugador j where j.name = ?1")
    fun buscarJugadorPorNombre(nombre: String): Jugador

    @Query("SELECT j FROM Jugador j where j.name = ?1 AND j.club.name = ?2")
    fun buscarJugadorPorNombreyEquipo(nombreJugador: String, nombreEquipo: String): Jugador
}

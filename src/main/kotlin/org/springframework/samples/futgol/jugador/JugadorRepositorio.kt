package org.springframework.samples.futgol.jugador

import org.springframework.data.repository.Repository

interface JugadorRepositorio : Repository<Jugador, Int> {

    fun findAll(): Collection<Jugador>

    fun save(jugador: Jugador)
}

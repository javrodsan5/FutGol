package org.springframework.samples.futgol.estadisticaJugador

import org.springframework.data.repository.Repository

interface EstadisticaJugadorRepositorio : Repository<EstadisticaJugador, Int> {

    fun findAll(): Collection<EstadisticaJugador>

    fun save(estadisticaJugador: EstadisticaJugador)

}

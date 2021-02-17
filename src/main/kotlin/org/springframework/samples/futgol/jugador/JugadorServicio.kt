package org.springframework.samples.futgol.jugador

import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.equipo.Equipo
import org.springframework.samples.futgol.equipo.EquipoRepositorio
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class JugadorServicio {

    private var jugadorRepositorio: JugadorRepositorio? = null

    @Transactional()
    @Throws(DataAccessException::class)
    fun saveJugador(jugador: Jugador) {
        jugadorRepositorio?.save(jugador)
    }
}

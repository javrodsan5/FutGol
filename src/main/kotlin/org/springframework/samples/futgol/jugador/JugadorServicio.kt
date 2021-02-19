package org.springframework.samples.futgol.jugador

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.equipo.Equipo
import org.springframework.samples.futgol.equipo.EquipoRepositorio
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class JugadorServicio {

    private var jugadorRepositorio: JugadorRepositorio? = null

    @Autowired
    fun JugadorServicio(jugadorRepositorio: JugadorRepositorio) {
        this.jugadorRepositorio = jugadorRepositorio
    }

    @Transactional()
    @Throws(DataAccessException::class)
    fun saveJugador(jugador: Jugador) {
        jugadorRepositorio?.save(jugador)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscaJugadorPorId(idJugador: Int): Jugador? {
        return jugadorRepositorio?.findById(idJugador)
    }
}

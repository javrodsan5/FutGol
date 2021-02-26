package org.springframework.samples.futgol.clausula

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.jugador.JugadorServicio
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ClausulaServicio {

    private var clausulaRepositorio: ClausulaRepositorio? = null

    @Autowired
    private var equipoServicio: EquipoServicio? = null

    @Autowired
    private var jugadorServicio: JugadorServicio? = null

    @Autowired
    fun ClausulaServicio(clausulaRepositorio: ClausulaRepositorio) {
        this.clausulaRepositorio = clausulaRepositorio
    }

    @Transactional()
    @Throws(DataAccessException::class)
    fun guardarClausula(clausula: Clausula) {
        clausulaRepositorio?.save(clausula)
    }

    @Transactional(readOnly = true)
    fun buscarClausulasPorIdJugador(idJugador: Int): Collection<Clausula>? {
        return clausulaRepositorio?.findClausulasByJugadorId(idJugador)
    }

    @Transactional(readOnly = true)
    fun buscarClausulasPorJugadorYEquipo(idJugador: Int, idEquipo: Int): Clausula? {
        var clausulasJugador = buscarClausulasPorIdJugador(idJugador)
        var clau = Clausula()
        if (clausulasJugador != null) {
            for (c in clausulasJugador) {
                if (c.equipo?.id == equipoServicio?.buscaEquiposPorId(idEquipo)?.id) {
                    return c
                }
            }
        }
        clau.equipo = equipoServicio?.buscaEquiposPorId(idEquipo)
        clau.jugador = jugadorServicio?.buscaJugadorPorId(idJugador)
        guardarClausula(clau)
        return clau
    }
}

package org.springframework.samples.futgol.puja

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PujaServicio {

    private var pujaRepositorio: PujaRepositorio? = null

    @Autowired
    fun PujaServicio(pujaRepositorio: PujaRepositorio) {
        this.pujaRepositorio = pujaRepositorio
    }

    @Transactional
    @Throws(DataAccessException::class)
    fun guardarPuja(puja: Puja) {
        pujaRepositorio?.save(puja)
    }

    @Transactional(readOnly = true)
    fun buscarPujasJugadorSubasta(idJugador: Int, idSubasta: Int): Collection<Puja>? {
        return pujaRepositorio?.buscarPujasJugadorSubasta(idJugador, idSubasta)
    }

    @Transactional
    fun borraPujaByEquipoJugadorSubasta(idEquipo: Int, idJugador: Int, idSubasta: Int): Unit? {
        return pujaRepositorio?.removePujaByEquipoJugadorSubasta(idEquipo, idJugador, idSubasta)
    }

    @Transactional(readOnly = true)
    fun existePujaEqJugSub(idEquipo: Int, idJugador: Int, idSubasta: Int): Boolean? {
        return pujaRepositorio?.existePujaEqJugSub(idEquipo, idJugador, idSubasta)
    }

    @Transactional(readOnly = true)
    fun existePujaJugSub(idJugador: Int, idSubasta: Int): Boolean? {
        return pujaRepositorio?.existePujaEqJug(idJugador, idSubasta)
    }
}

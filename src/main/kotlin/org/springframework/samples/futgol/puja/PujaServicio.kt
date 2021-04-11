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
    fun buscarSubastaPorLigaId(idPuja: Int): Puja? {
        return pujaRepositorio?.findPujaById(idPuja)
    }

    @Transactional
    fun borraPujaPorEquipoYJugador(idEquipo: Int, idJugador: Int): Unit? {
        return pujaRepositorio?.removePujaByEquipoIdAndJugadorId(idEquipo, idJugador)
    }

    @Transactional(readOnly = true)
    fun existePujaPorEquipoyJugador(idEquipo: Int, idJugador: Int): Boolean? {
        return pujaRepositorio?.existePujaEquipo(idEquipo, idJugador)
    }
}

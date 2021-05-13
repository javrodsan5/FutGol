package org.springframework.samples.futgol.intercambio

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.puja.Puja
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class IntercambioServicio {

    private var intercambioRepositorio: IntercambioRepositorio? = null

    @Autowired
    fun IntercambioServicio(intercambioRepositorio: IntercambioRepositorio) {
        this.intercambioRepositorio = intercambioRepositorio
    }

    @Transactional
    @Throws(DataAccessException::class)
    fun guardarIntercambio(intercambio: Intercambio) {
        intercambioRepositorio?.save(intercambio)
    }

    @Transactional(readOnly = true)
    fun buscarIntercambiosEquipo(idEquipo: Int): Collection<Intercambio>? {
        return intercambioRepositorio?.buscarIntercambiosPorIdEquipo(idEquipo)
    }

    @Transactional(readOnly = true)
    fun buscaIntercambioPorId(idIntercambio: Int): Intercambio? {
        return intercambioRepositorio?.buscaIntercambioPorId(idIntercambio)
    }

    @Transactional(readOnly = true)
    fun existenIntercambiosEquipo(idEquipo: Int): Boolean? {
        return intercambioRepositorio?.existenIntercambiosPorIdEquipo(idEquipo)
    }

    @Transactional(readOnly = true)
    fun existeIntercambioPorIdIntercIdEquipo(idIntercambio: Int, idEquipo: Int): Boolean? {
        return intercambioRepositorio?.existeIntercambioPorIdIntercIdEquipo(idIntercambio, idEquipo)
    }

    @Transactional
    fun borraIntercambio(idIntercambio: Int): Unit? {
        return intercambioRepositorio?.removeIntercambioById(idIntercambio)
    }
}

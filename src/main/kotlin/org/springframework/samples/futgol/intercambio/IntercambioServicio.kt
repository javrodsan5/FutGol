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
    fun buscarIntercambiosUsuario(idUsuario: Int): Collection<Intercambio>? {
        return intercambioRepositorio?.buscarIntercambiosPorIdUsuario(idUsuario)
    }

    @Transactional(readOnly = true)
    fun existenIntercambiosUsuario(idUsuario: Int): Boolean? {
        return intercambioRepositorio?.existenIntercambiosPorIdUsuario(idUsuario)
    }

    @Transactional
    fun borraIntercambio(idIntercambio: Int): Unit? {
        return intercambioRepositorio?.removeIntercambioById(idIntercambio)
    }
}

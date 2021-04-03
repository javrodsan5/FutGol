package org.springframework.samples.futgol.puntosJornadaEquipo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PtosJornadaEquipoServicio {

    private var ptosJornadaEquipoRepositorio: PtosJornadaEquipoRepositorio? = null

    @Autowired
    fun PtosJornadaEquipoServicio(ptosJornadaEquipoRepositorio: PtosJornadaEquipoRepositorio?) {
        this.ptosJornadaEquipoRepositorio = ptosJornadaEquipoRepositorio
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscarPtosJEPorEquipo(idEquipo: Int): Collection<PtosJornadaEquipo>? {
        return ptosJornadaEquipoRepositorio?.buscarPtosPorEquipo(idEquipo)
    }

    @Transactional
    @Throws(DataAccessException::class)
    fun guardarPtosJornadaEquipo(ptosJornadaEquipo: PtosJornadaEquipo) {
        ptosJornadaEquipoRepositorio?.save(ptosJornadaEquipo)
    }
}

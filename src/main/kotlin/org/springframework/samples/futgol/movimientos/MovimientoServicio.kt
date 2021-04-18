package org.springframework.samples.futgol.movimientos

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.login.User
import org.springframework.samples.futgol.usuario.Usuario
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MovimientoServicio {

    private var movimientoRepositorio: MovimientoRepositorio? = null


    @Autowired
    private val equipoServicio: EquipoServicio? = null

    @Autowired
    fun MovimientoServicio(movimientoRepositorio: MovimientoRepositorio) {
        this.movimientoRepositorio = movimientoRepositorio
    }

    @Transactional(readOnly = true)
    fun buscarMovimientosDeLigaPorId(idLiga: Int): Collection<Movimiento>? {
        return movimientoRepositorio?.buscarMovimientosDeLigaPorId(idLiga)
    }

    @Transactional()
    @Throws(DataAccessException::class)
    fun guardarMovimiento(movimiento: Movimiento) {
        movimientoRepositorio?.save(movimiento)
    }

    @Transactional(readOnly = true)
    fun buscarMovimientosPorUsuarioYLiga(nombreUsuario: String, idLiga: Int): Collection<Movimiento>? {
        return movimientoRepositorio?.buscarMovimientosPorUsuarioYLiga(nombreUsuario,idLiga)
    }
}

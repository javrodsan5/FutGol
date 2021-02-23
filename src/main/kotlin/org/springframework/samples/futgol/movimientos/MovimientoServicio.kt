package org.springframework.samples.futgol.movimientos

import org.springframework.beans.factory.annotation.Autowired
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

    @Transactional(readOnly = true)
    fun buscarMovimientosUsuario(nombreUsuario: String): Collection<Movimiento>? {
        return movimientoRepositorio?.buscarMovimientosPorUsuario(nombreUsuario)
    }

    @Transactional(readOnly = true)
    fun buscaVendedor(nombreJugador: String): Usuario? {
        var equipos = equipoServicio?.buscaTodosEquipos()
        if (equipos != null) {
            for (e in equipos) {
                for (jugador in e.jugadores) {
                    if (jugador.name == nombreJugador) {
                        return e.usuario
                    }
                }
            }
        }
        var usuario = Usuario()
        var user = User()
        usuario.user = user
        usuario.user?.username = "En libertad"
        return usuario
    }

}

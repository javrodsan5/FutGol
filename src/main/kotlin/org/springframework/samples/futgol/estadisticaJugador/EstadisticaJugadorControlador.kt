package org.springframework.samples.futgol.estadisticaJugador

import org.springframework.samples.futgol.equipoReal.EquipoRealServicio
import org.springframework.samples.futgol.jugador.JugadorServicio
import org.springframework.samples.futgol.partido.PartidoServicio
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@Controller
class EstadisticaJugadorControlador(
    val estadisticaJugadorServicio: EstadisticaJugadorServicio,
    val partidoServicio: PartidoServicio,
    val jugadorServicio: JugadorServicio,
    val equipoRealServicio: EquipoRealServicio
) {

    private val VISTA_WSESTADISTICA = "estadisticaJugador/wsEstadistica"

    @GetMapping("/WSEstadisticas")
    fun iniciaWSJugadores(model: Model): String {
        return VISTA_WSESTADISTICA
    }

    @PostMapping("/WSEstadisticas")
    fun creaEstadisticasJugadoresWS(model: Model): String {
        this.estadisticaJugadorServicio.wsEstadisticas()
        this.estadisticaJugadorServicio.wsValoraciones()
        return VISTA_WSESTADISTICA
    }

}

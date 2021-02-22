package org.springframework.samples.futgol.estadisticaJugador

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@Controller
class EstadisticaJugadorControlador(val estadisticaJugadorServicio: EstadisticaJugadorServicio) {


    private val VISTA_WSESTADISTICA = "estadisticaJugador/wsEstadistica"


    @GetMapping("/WSEstadisticas")
    fun iniciaWSJugadores(model: Model): String {
        return VISTA_WSESTADISTICA
    }

    @PostMapping("/WSEstadisticas")
    fun creaWSJugadores(model: Model): String {
        this.estadisticaJugadorServicio.equiposPartidosEstadisticasJugadores()
        return VISTA_WSESTADISTICA
    }
}

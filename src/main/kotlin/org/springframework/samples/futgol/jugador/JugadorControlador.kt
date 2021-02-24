package org.springframework.samples.futgol.jugador

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping

@Controller
class JugadorControlador(val jugadorServicio: JugadorServicio) {

    private val VISTA_WSJUGADORES = "jugadores/wsJugadores"
    private val VISTA_DETALLES_JUGADOR = "jugadores/detallesJugador"


    @GetMapping("/WSJugadores")
    fun iniciaWSJugadores(model: Model): String {
        return VISTA_WSJUGADORES
    }

    @PostMapping("/WSJugadores")
    fun creaWSJugadores(model: Model): String {
        this.jugadorServicio.webScrapingJugadoresTransfermarkt()
        this.jugadorServicio.webScrapingJugadoresFbref()
        return VISTA_WSJUGADORES
    }

    @GetMapping("/jugador/{idJugador}")
    fun detallesJugador(model: Model, @PathVariable("idJugador") idJugador: Int): String {
        var jugador = jugadorServicio.buscaJugadorPorId(idJugador)
        if (jugador != null) {
            model["jugador"] = jugador
        }
        return VISTA_DETALLES_JUGADOR
    }


}

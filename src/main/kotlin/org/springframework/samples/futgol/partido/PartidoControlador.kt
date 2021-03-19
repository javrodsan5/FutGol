package org.springframework.samples.futgol.partido

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@Controller
class PartidoControlador(val partidoServicio: PartidoServicio) {

    private val VISTA_WSPARTIDOS = "partidos/wsPartidos"

    @GetMapping("/WSPartidos")
    fun iniciaWSPartidos(model: Model): String {
        return VISTA_WSPARTIDOS
    }

    @PostMapping("/WSPartidos")
    fun creaWSPartidos(model: Model): String {
        this.partidoServicio.wsPartidos()
        return VISTA_WSPARTIDOS
    }
}

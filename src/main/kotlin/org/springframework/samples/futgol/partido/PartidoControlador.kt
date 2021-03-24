package org.springframework.samples.futgol.partido

import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@EnableScheduling
@Controller
class PartidoControlador(val partidoServicio: PartidoServicio) {

    private val VISTA_WSPARTIDOS = "partidos/wsPartidos"

    @GetMapping("/WSPartidos")
    fun iniciaWSPartidos(model: Model): String {
        return VISTA_WSPARTIDOS
    }

    @Scheduled(cron = "0 50 17 * * ? ")
    @PostMapping("/WSPartidos")
    fun creaWSPartidos(): String {
        this.partidoServicio.wsPartidos()
        return VISTA_WSPARTIDOS
    }
}

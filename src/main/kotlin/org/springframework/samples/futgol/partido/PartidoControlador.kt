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

    @Scheduled(cron = "0 47 18 * * ? ")
    @GetMapping("/WSPartidos")
    fun creaWSPartidos(): String {
        this.partidoServicio.wsPartidos()
        return "welcome"
    }
}

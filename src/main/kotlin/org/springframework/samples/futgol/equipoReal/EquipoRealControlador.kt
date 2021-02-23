package org.springframework.samples.futgol.equipoReal

import org.jsoup.Jsoup
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@Controller
class EquipoRealControlador(val equipoRealServicio: EquipoRealServicio) {

    private val VISTA_WSEQUIPOS = "equipos/WScreaEquipos"

    @GetMapping("/WSEquipos")
    fun iniciaWSEquipos(model: Model): String {
        return VISTA_WSEQUIPOS
    }

    @PostMapping("/WSEquipos")
    fun creaWSEquipos(model: Model): String {
        this.equipoRealServicio.webScrapingEquipos()
        return VISTA_WSEQUIPOS
    }

}

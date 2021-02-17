package org.springframework.samples.futgol.equipo

import org.springframework.samples.futgol.liga.LigaServicio
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping

@Controller
class EquipoControlador(val ligaServicio: LigaServicio, val equipoServicio: EquipoServicio) {

    private val VISTA_WSEQUIPOS = "equipos/wsEquipos"


    @GetMapping("/getEquipos")
    fun wsEquipos(model: Model): String {
        var numEquipos = equipoServicio.guardarEquipos()
        model["numEquipos"] = numEquipos
        return VISTA_WSEQUIPOS
    }

}

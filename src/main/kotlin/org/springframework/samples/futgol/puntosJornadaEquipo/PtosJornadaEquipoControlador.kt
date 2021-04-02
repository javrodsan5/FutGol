package org.springframework.samples.futgol.puntosJornadaEquipo

import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class PtosJornadaEquipoControlador(val ptosJornadaEquipoServicio: PtosJornadaEquipoServicio, val equipoServicio: EquipoServicio) {


    @GetMapping("/tututu")
    fun asignarPuntosEquipo2(): String {
        this.equipoServicio.asignaPuntosEquipo("aaaa", 27)
        return "equipos/borrar"

    }

}

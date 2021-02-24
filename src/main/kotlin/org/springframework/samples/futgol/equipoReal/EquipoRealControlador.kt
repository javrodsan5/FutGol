package org.springframework.samples.futgol.equipoReal

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping

@Controller
class EquipoRealControlador(val equipoRealServicio: EquipoRealServicio) {

    private val VISTA_WSEQUIPOS = "equipos/WScreaEquipos"
    private val VISTA_LISTA_EQUIPOSREALES = "equiposReales/listaEquiposReales"
    private val VISTA_DETALLES_EQUIPOREAL = "equiposReales/detallesEquipoReal"

    @GetMapping("/WSEquipos")
    fun iniciaWSEquipos(model: Model): String {
        return VISTA_WSEQUIPOS
    }

    @PostMapping("/WSEquipos")
    fun creaWSEquipos(model: Model): String {
        this.equipoRealServicio.webScrapingEquipos()
        return VISTA_WSEQUIPOS
    }

    @GetMapping("/equiposLiga")
    fun listaEquiposReales(model: Model): String {
        model["equiposReales"] = equipoRealServicio.buscarTodosEquiposReales()!!
        return VISTA_LISTA_EQUIPOSREALES
    }

    @GetMapping("/equiposLiga/{nombreEquipo}")
    fun detallesEquipoReal(model: Model, @PathVariable("nombreEquipo") nombreEquipo: String): String {
        var equipo = equipoRealServicio.buscarEquipoRealPorNombre(nombreEquipo)!!
        model["equipo"] = equipo
        var jugadores = equipo.jugadores
        model["partidosLocal"] = equipo.partidosLocal
        model["partidosVisitante"] = equipo.partidosVisitante
        model["jugadores"] = jugadores
        if (jugadores.size > 5) {
            model["top5Jugadores"] = equipo.jugadores.sortedBy { x -> -x.puntos }.subList(0, 5)
        }
        return VISTA_DETALLES_EQUIPOREAL
    }

}

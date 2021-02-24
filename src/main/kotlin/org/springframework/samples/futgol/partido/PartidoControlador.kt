package org.springframework.samples.futgol.partido

import org.springframework.samples.futgol.estadisticaJugador.EstadisticaJugadorServicio
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping

@Controller
class PartidoControlador(val partidoServicio: PartidoServicio) {

    private val VISTA_WSPARTIDOS = "partidos/wsPartidos"
    private val VISTA_PARTIDOS = "partidos/partidos"
    private val VISTA_FILTRAR_PARTIDOS = "partidos/filtroPartidos"


    @GetMapping("/WSPartidos")
    fun iniciaWSPartidos(model: Model): String {
        return VISTA_WSPARTIDOS
    }

    @GetMapping("/jornadas/{jornada}")
    fun partidosPorJornada(model: Model, @PathVariable(("jornada")) jornada: Int): String {
        model["partidos"] = this.partidoServicio.buscarTodosPartidosPorJornada(jornada)!!
        return VISTA_PARTIDOS
    }

    @GetMapping("/jornadas")
    fun partidos(model: Model): String {
        model["jornadas"] = this.partidoServicio.buscarJornadas()!!
        return VISTA_FILTRAR_PARTIDOS
    }

    @PostMapping("/WSPartidos")
    fun creaWSPartidos(model: Model): String {
        this.partidoServicio.wsPartidos()
        return VISTA_WSPARTIDOS
    }
}

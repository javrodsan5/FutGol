package org.springframework.samples.futgol.jornadas

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class JornadaControlador(val jornadaServicio: JornadaServicio) {

    private val VISTA_JORNADAS = "jornadas/listaJornadas"
    private val VISTA_DETALLES_JORNADA = "jornadas/detallesJornada"


    @GetMapping("/jornadas")
    fun listaJornadas(model: Model): String {
        model["jornadas"] = jornadaServicio.buscarTodasJornadas()!!
        return VISTA_JORNADAS
    }

    @GetMapping("/jornada/{jornadaId}")
    fun detallesJornada(model: Model, @PathVariable("jornadaId") jornadaId: Int): String {
        var jornada = jornadaServicio.buscarJornadaPorId(jornadaId)
        if (jornada != null) {
            model["jornada"] = jornada
            model["jornadas"] = jornadaServicio.buscarTodasJornadas()!!
            model["jugadores"] = jornadaServicio.onceIdealJornada(jornadaId)!!
            model["partidos"] = jornada.partidos
        }
        return VISTA_DETALLES_JORNADA
    }
}

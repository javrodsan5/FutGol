package org.springframework.samples.futgol.jornadas

import org.springframework.cache.annotation.CachePut
import org.springframework.samples.futgol.estadisticaJugador.EstadisticaJugadorServicio
import org.springframework.samples.futgol.partido.PartidoServicio
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.stream.Collectors

@Controller
class JornadaControlador(
    val jornadaServicio: JornadaServicio,
    val partidoServicio: PartidoServicio,
    val estadisticaJugadorServicio: EstadisticaJugadorServicio
) {

    private val VISTA_DETALLES_JORNADA = "jornadas/detallesJornada"

    @CachePut("jornadas")
    @GetMapping("/jornadas")
    fun ultimaJornadaJugada(model: Model): String {
        model["jornadas"] = jornadaServicio.buscarTodasJornadas()!!
        var partidosJugados = partidoServicio.buscarTodosPartidos()
            ?.stream()?.filter { x -> x.resultado != "" }?.collect(Collectors.toList())
        var ultimaJornada = partidosJugados?.stream()
            ?.max(Comparator.comparing { x -> x.jornada!!.numeroJornada })?.get()?.jornada?.numeroJornada
        var partidosUJ = partidoServicio.buscarTodosPartidos()
            ?.stream()?.filter { x -> x.jornada?.numeroJornada == ultimaJornada }?.collect(Collectors.toList())
        var todosPartidosJugados = partidosUJ?.stream()?.allMatch { x -> x.resultado != "" }
        if (todosPartidosJugados == false) {
            if (ultimaJornada != null) {
                ultimaJornada -= 1
            }
        }
        var jornada = ultimaJornada?.let { jornadaServicio.buscarJornadaPorNumeroJornada(it) }
        if (jornada != null) {
            model["jornada"] = jornada
            model["jornadas"] = jornadaServicio.buscarTodasJornadas()!!
            var jugadores = jornada.id?.let { jornadaServicio.onceIdealJornada(it) }!!
            if (jugadores.isEmpty()) {
                model["noOnce"] = true
            } else {
                model["jugadores"] = jugadores
                model["noOnce"] = false
            }
            model["partidos"] = this.partidoServicio.buscarTodosPartidos()
                ?.stream()?.filter { x -> x.jornada?.id == jornada.id }?.collect(Collectors.toList())!!
            model["ultimaJornada"] = true
        }
        return VISTA_DETALLES_JORNADA
    }

    @GetMapping("/jornada/{jornadaId}")
    fun detallesJornada(model: Model, @PathVariable("jornadaId") jornadaId: Int): String {
        var jornada = jornadaServicio.buscarJornadaPorId(jornadaId)
        if (jornada != null) {
            model["jornada"] = jornada
            model["jornadas"] = jornadaServicio.buscarTodasJornadas()!!
            if (jornada.jugadores.isEmpty()) {
                var jugadores = jornada.id?.let { jornadaServicio.onceIdealJornada(it) }!!
                if (jugadores.isEmpty()) {
                    model["noOnce"] = true
                } else {
                    for (j in jugadores) {
                        jornada.jugadores.add(j!!)
                    }
                    jornadaServicio.guardarJornada(jornada)
                    model["jugadores"] = jornada.jugadores
                    model["noOnce"] = false
                }
            } else {
                model["jugadores"] = jornada.jugadores
                model["noOnce"] = false
            }
            //PROVISIONAL
            model["partidos"] = this.partidoServicio.buscarTodosPartidos()
                ?.stream()?.filter { x -> x.jornada?.id == jornadaId }?.collect(Collectors.toList())!!
        }
        return VISTA_DETALLES_JORNADA
    }
}

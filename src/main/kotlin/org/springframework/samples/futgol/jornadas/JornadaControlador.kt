package org.springframework.samples.futgol.jornadas

import org.springframework.cache.annotation.CachePut
import org.springframework.samples.futgol.equipoReal.EquipoRealServicio
import org.springframework.samples.futgol.estadisticaJugador.EstadisticaJugadorServicio
import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.samples.futgol.partido.PartidoServicio
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class JornadaControlador(
    val jornadaServicio: JornadaServicio,
    val partidoServicio: PartidoServicio,
    val estadisticaJugadorServicio: EstadisticaJugadorServicio,
    val equipoRealServicio: EquipoRealServicio

) {

    private val VISTA_DETALLES_JORNADA = "jornadas/detallesJornada"

    @CachePut("jornadas")
    @GetMapping("/jornadas")
    fun ultimaJornadaJugada(model: Model): String {
        var b = true
        var jornada = this.jornadaServicio.buscarTodasJornadas()?.stream()
            ?.filter { x -> x.partidos.stream().allMatch { p -> p.resultado != "" } }
            ?.max(Comparator.comparing { x -> x.numeroJornada })?.get()
        if (jornada != null) {
            model["jornada"] = jornada
            var jornadaId = jornada.id
            model["ultimaJornada"] = true
            model["jornadas"] = jornadaServicio.buscarTodasJornadas()!!

            if (jornadaId?.let { this.estadisticaJugadorServicio.existeEstadisticasJornada(it) } == true) {
                if (jornada.jugadores.isEmpty()) {
                    if (jornada.mejorJugador == null) {
                        var mejorJugador = this.estadisticaJugadorServicio.mejorJugadorJornada(jornadaId)
                        jornada.mejorJugador = mejorJugador
                        jornadaServicio.guardarJornada(jornada)
                    }

                    var todosJugadores = this.estadisticaJugadorServicio.jugadoresParticipantesEnJornada(jornadaId)

                    var map: Map<String, MutableList<Jugador?>> = HashMap()
                    if ((todosJugadores != null && todosJugadores.isNotEmpty()) && (jornada.formacion == "" || jornada.formacion == null)) {
                        map = jornadaServicio.onceIdeal(todosJugadores, jornadaId, "")

                    }

                    var jugadores: MutableList<Jugador?> = ArrayList()
                    var formacion = ""
                    if (map.values.isNotEmpty()) {
                        jugadores = map.values.first()
                        formacion = map.keys.first()
                    }

                    if (jugadores.isNotEmpty()) {
                        jornada.jugadores = jugadores as MutableList<Jugador>
                        jornada.formacion = formacion
                        jornadaServicio.guardarJornada(jornada)
                        b = false
                    }
                } else {
                    b = false
                }
            }
            model["noOnce"] = b
        }
        return VISTA_DETALLES_JORNADA
    }

    @GetMapping("/jornada/{jornadaId}")
    fun detallesJornada(model: Model, @PathVariable("jornadaId") jornadaId: Int): String {
        if (this.jornadaServicio.buscarTodasJornadas()?.any { x -> x.id == jornadaId } == true) {
            var noOnceTodavia = true
            var jornada = jornadaServicio.buscarJornadaPorId(jornadaId)
            if (jornada != null) {
                model["jornada"] = jornada
            }
            model["jornadas"] = jornadaServicio.buscarTodasJornadas()!!
            if (jornada?.partidos?.stream()?.allMatch { p -> p.resultado != "" } == true) {
                if (this.estadisticaJugadorServicio.existeEstadisticasJornada(jornadaId) == true) {
                    if (jornada.jugadores.isEmpty() == true) {
                        if (jornada.mejorJugador == null) {
                            jornada.mejorJugador = this.estadisticaJugadorServicio.mejorJugadorJornada(jornadaId)
                            jornadaServicio.guardarJornada(jornada)
                        }

                        var todosJugadores = this.estadisticaJugadorServicio.jugadoresParticipantesEnJornada(jornadaId)
                        var map: Map<String, MutableList<Jugador?>> = HashMap()
                        if ((todosJugadores != null && todosJugadores.isNotEmpty()) && (jornada.formacion == "" || jornada.formacion == null)) {
                            map = jornadaServicio.onceIdeal(todosJugadores, jornadaId, "")
                        }

                        var jugadores: MutableList<Jugador?> = ArrayList()
                        var formacion = ""
                        if (map.values.isNotEmpty()) {
                            jugadores = map.values.first()
                            formacion = map.keys.first()
                        }

                        if (jugadores.isNotEmpty()) {
                            jornada.jugadores = jugadores as MutableList<Jugador>
                            println(jornada.jugadores)
                            jornada.formacion = formacion

                            jornadaServicio.guardarJornada(jornada)
                            noOnceTodavia = false
                        }
                    } else {
                        println(jornada.jugadores)
                        noOnceTodavia = false
                    }
                }
            }
            model["noOnce"] = noOnceTodavia
        }
        return VISTA_DETALLES_JORNADA
    }
}

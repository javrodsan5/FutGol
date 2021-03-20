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
import java.util.stream.Collectors

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
        var ultimaJ = ultimaJornada?.let { jornadaServicio.buscarJornadaPorNumeroJornada(it) }
        if (ultimaJ != null) {
            var jornadaId = ultimaJ.id
            if (this.jornadaServicio.buscarTodasJornadas()?.any { x -> x.id == jornadaId } == true) {
                var jornada = jornadaId?.let { jornadaServicio.buscarJornadaPorId(it) }
                if (jornada != null) {
                    model["jornada"] = jornada
                }
                model["ultimaJornada"] = true
                model["jornadas"] = jornadaServicio.buscarTodasJornadas()!!
                model["partidos"] = this.partidoServicio.buscarTodosPartidos()
                    ?.stream()?.filter { x -> x.jornada?.id == jornadaId }?.collect(Collectors.toList())!!

                if (jornadaId?.let { this.estadisticaJugadorServicio.existeEstadisticasJornada(it) } == true) {

                    if (jornada?.jugadores?.isEmpty() == true) {

                        if (jornada.mejorJugador == null) {
                            var mejorJugador = this.estadisticaJugadorServicio.buscarTodasEstadisticas()
                                ?.stream()?.filter { x -> x.partido?.jornada?.id == jornadaId }
                                ?.sorted(Comparator.comparing { x -> -x.puntos })?.findFirst()?.orElse(null)!!
                            jornada.mejorJugador = mejorJugador
                            jornadaServicio.guardarJornada(jornada)
                        }

                        val todosJugadores = this.estadisticaJugadorServicio.buscarTodasEstadisticas()
                            ?.stream()?.filter { x -> x.partido?.jornada?.id == jornadaId }
                            ?.sorted(Comparator.comparing { x -> -x.puntos })
                            ?.map { x -> x.jugador }?.collect(Collectors.toList())

                        var map = jornadaServicio.onceIdeal(todosJugadores, jornadaId, "") //mirar

                        var jugadores: MutableList<Jugador?> = ArrayList<Jugador?>()
                        var formacion = ""
                        if (map.values.isNotEmpty()) {
                            jugadores = map.values.first()
                            formacion = map.keys.first()
                        }

                        if (jugadores.isEmpty()) {
                            model["noOnce"] = true
                        } else {
                            for (j in jugadores) {
                                jornada.jugadores.add(j!!)
                            }
                            jornada.formacion = formacion
                            jornadaServicio.guardarJornada(jornada)
                            model["formacion"] = jornada.formacion
                            model["jugadores"] = jornada.jugadores
                            model["noOnce"] = false
                        }
                    } else {
                        model["formacion"] = jornada?.formacion!!
                        model["jugadores"] = jornada.jugadores
                        model["noOnce"] = false

                    }
                } else {
                    model["noOnce"] = true
                }
            } else {
                return "redirect:/"
            }
        }
        return VISTA_DETALLES_JORNADA
    }

    @GetMapping("/jornada/{jornadaId}")
    fun detallesJornada(model: Model, @PathVariable("jornadaId") jornadaId: Int): String {
        if (this.jornadaServicio.buscarTodasJornadas()?.any { x -> x.id == jornadaId } == true) {
            var jornada = jornadaServicio.buscarJornadaPorId(jornadaId)
            if (jornada != null) {
                model["jornada"] = jornada
            }
            model["jornadas"] = jornadaServicio.buscarTodasJornadas()!!
            model["partidos"] = this.partidoServicio.buscarTodosPartidos()
                ?.stream()?.filter { x -> x.jornada?.id == jornadaId }?.collect(Collectors.toList())!!

            if (this.estadisticaJugadorServicio.existeEstadisticasJornada(jornadaId) == true) {

                if (jornada?.jugadores?.isEmpty() == true) {
                    if (jornada.mejorJugador == null) {
                        var mejorJugador = this.estadisticaJugadorServicio.buscarTodasEstadisticas()
                            ?.stream()?.filter { x -> x.partido?.jornada?.id == jornadaId }
                            ?.sorted(Comparator.comparing { x -> -x.puntos })?.findFirst()?.orElse(null)!!
                        jornada.mejorJugador = mejorJugador
                        jornadaServicio.guardarJornada(jornada)
                    }

                    var todosJugadores = this.estadisticaJugadorServicio.buscarTodasEstadisticas()
                        ?.stream()?.filter { x -> x.partido?.jornada?.id == jornadaId }
                        ?.sorted(Comparator.comparing { x -> -x.puntos })
                        ?.map { x -> x.jugador }?.collect(Collectors.toList())

                    var map = jornadaServicio.onceIdeal(todosJugadores, jornadaId, "") //mirar

                    var jugadores: MutableList<Jugador?> = ArrayList<Jugador?>()
                    var formacion = ""
                    if (map.values.isNotEmpty()) {
                        jugadores = map.values.first()
                        formacion = map.keys.first()
                    }

                    if (jugadores.isEmpty()) {
                        model["noOnce"] = true
                    } else {
                        for (j in jugadores) {
                            jornada.jugadores.add(j!!)
                        }
                        jornada.formacion = formacion
                        jornadaServicio.guardarJornada(jornada)
                        model["formacion"] = jornada.formacion
                        model["jugadores"] = jornada.jugadores
                        model["noOnce"] = false
                    }
                } else {
                    model["formacion"] = jornada?.formacion!!
                    model["jugadores"] = jornada.jugadores
                    model["noOnce"] = false

                }
            } else {
                model["noOnce"] = true
            }
        } else {
            return "redirect:/"
        }
        return VISTA_DETALLES_JORNADA
    }
}

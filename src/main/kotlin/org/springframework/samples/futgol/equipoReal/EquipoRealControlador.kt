package org.springframework.samples.futgol.equipoReal

import org.springframework.cache.annotation.CachePut
import org.springframework.samples.futgol.jornadas.JornadaServicio
import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.samples.futgol.util.MetodosAux
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@EnableScheduling
@Controller
class EquipoRealControlador(val equipoRealServicio: EquipoRealServicio, val jornadaServicio: JornadaServicio) {

    private val VISTA_LISTA_EQUIPOSREALES = "equiposReales/listaEquiposReales"
    private val VISTA_DETALLES_EQUIPOREAL = "equiposReales/detallesEquipoReal"

    @CachePut("listaEquiposReales")
    @GetMapping("/equiposLiga")
    fun listaEquiposReales(model: Model): String {
        model["equiposReales"] = equipoRealServicio.buscarTodosEquiposReales()!!.sortedBy { x -> x.posicion }
        return VISTA_LISTA_EQUIPOSREALES
    }

    @CachePut("detallesEquipoReal")
    @GetMapping("/equiposLiga/{nombreEquipo}")
    fun detallesEquipoReal(model: Model, @PathVariable("nombreEquipo") nombreEquipo: String): String {
        if (equipoRealServicio.existeEquipoReal(nombreEquipo) == true) {
            var equipo = equipoRealServicio.buscarEquipoRealPorNombre(nombreEquipo)!!
            model["equipo"] = equipo
            var todosJugadores = equipo.jugadores.sortedBy { x -> -x.puntos }

            var map: Map<String, MutableList<Jugador?>> = HashMap()
            if (todosJugadores != null && todosJugadores.isNotEmpty()) {
                map = equipo.name?.let { this.jornadaServicio.onceIdeal(todosJugadores, 0, it) }!!
            }
            var jugadores: MutableList<Jugador?> = ArrayList()
            var formacion: String

            if (map.values.isNotEmpty()) {
                jugadores = map.values.first()
                var onceOrdenado = jugadores.sortedByDescending { x -> MetodosAux().transformador(x?.posicion!!) }
                var onceOrdenadoMut: MutableList<Jugador> = ArrayList()

                onceOrdenadoMut.addAll(onceOrdenado as Collection<Jugador>)
                model["jugadores"] = onceOrdenadoMut

                formacion = map.keys.first()
                equipo.formacion = formacion
                model["formacion"] = equipo.formacion

                this.equipoRealServicio.guardarEquipo(equipo)
            }

            var proxPartido = equipoRealServicio.proximoPartido(nombreEquipo)
            model["proxPartido"] = proxPartido
            var b=true
            if (proxPartido.equipoVisitante?.name == equipo.name) {
                model["rival"] = proxPartido.equipoLocal!!
                b=false
            } else {
                model["rival"] = proxPartido.equipoVisitante!!
            }
            model["equipoEsLocal"] = b
            model["partidosLocal"] = equipo.partidosLocal.sortedBy { x -> x.jornada?.numeroJornada }
            model["partidosVisitante"] = equipo.partidosVisitante.sortedBy { x -> x.jornada?.numeroJornada }

            if (equipo.jugadores.size > 5) {
                model["top5Jugadores"] = equipo.jugadores.sortedBy { x -> -x.puntos }.subList(0, 5)
            }

            var banquillo = equipo.jugadores
            banquillo.removeAll(jugadores)

            var banquilloOrdenadoMut: MutableList<Jugador> = ArrayList()
            banquilloOrdenadoMut.addAll(banquillo.sortedByDescending { x -> MetodosAux().transformador(x.posicion) })

            model["banquillo"] = banquilloOrdenadoMut
            model["miEquipo"] = false
            model["equipoReal"] = true
            model["otroEquipo"] = false
        } else {
            return "redirect:/equiposLiga"
        }
        return VISTA_DETALLES_EQUIPOREAL
    }
}

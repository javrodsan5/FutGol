package org.springframework.samples.futgol.equipoReal

import org.springframework.cache.annotation.CachePut
import org.springframework.samples.futgol.jornadas.JornadaServicio
import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import java.util.stream.Collectors

@Controller
class EquipoRealControlador(val equipoRealServicio: EquipoRealServicio, val jornadaServicio: JornadaServicio) {

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
            var todosJugadores = equipo.jugadores
                ?.stream()?.sorted(Comparator.comparing { x -> x.puntos })
                ?.collect(Collectors.toList())?.reversed()
            var map = equipo.name?.let { this.jornadaServicio.onceIdeal(todosJugadores,0, it) }
            println("womazo")
            var jugadores: MutableList<Jugador?> = ArrayList<Jugador?>()
            var formacion = ""
            println(map?.keys)
            println(map?.values)
            if(map?.values?.isNotEmpty()==true){
                jugadores= map?.values?.first()
                model["jugadores"]= jugadores
                println(jugadores)
                formacion= map?.keys.first()
                equipo.formacion= formacion
                println(formacion)
                this.equipoRealServicio.guardarEquipo(equipo)
                model["formacion"] = equipo.formacion
            }
            model["partidosLocal"] = equipo.partidosLocal.sortedBy { x -> x.jornada?.numeroJornada }
            model["partidosVisitante"] = equipo.partidosVisitante.sortedBy { x -> x.jornada?.numeroJornada }
            if (equipo.jugadores.size > 5) {
                model["top5Jugadores"] = equipo.jugadores.sortedBy { x -> -x.puntos }.subList(0, 5)
            }
            var banquillo = equipo.jugadores
            banquillo.removeAll(jugadores)
            model["banquillo"] = banquillo

        } else {
            return "redirect:/equiposLiga"
        }
        return VISTA_DETALLES_EQUIPOREAL
    }
}

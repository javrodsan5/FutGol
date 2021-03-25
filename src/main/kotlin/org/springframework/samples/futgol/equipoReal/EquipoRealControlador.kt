package org.springframework.samples.futgol.equipoReal

import org.springframework.cache.annotation.CachePut
import org.springframework.samples.futgol.jornadas.JornadaServicio
import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import java.util.stream.Collectors

@EnableScheduling
@Controller
class EquipoRealControlador(val equipoRealServicio: EquipoRealServicio, val jornadaServicio: JornadaServicio) {

    private val VISTA_LISTA_EQUIPOSREALES = "equiposReales/listaEquiposReales"
    private val VISTA_DETALLES_EQUIPOREAL = "equiposReales/detallesEquipoReal"

    @Scheduled(cron = "0 45 18 * * ? ")
    @GetMapping("/WSEquipos")
    fun creaWSEquipos(): String {
        this.equipoRealServicio.webScrapingEquipos()
        return "welcome"
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
                .stream().sorted(Comparator.comparing { x -> -x.puntos })
                .collect(Collectors.toList())

            var map: Map<String, MutableList<Jugador?>> = HashMap()
            if(todosJugadores!=null && todosJugadores.isNotEmpty()){
                map = equipo.name?.let { this.jornadaServicio.onceIdeal(todosJugadores,0, it) }!!
            }
            var jugadores: MutableList<Jugador?> = ArrayList()
            var formacion: String

            if(map?.values?.isNotEmpty()==true){
                jugadores= map.values.first()
                model["jugadores"]= jugadores

                formacion= map.keys.first()
                equipo.formacion= formacion
                model["formacion"] = equipo.formacion

                this.equipoRealServicio.guardarEquipo(equipo)
            }
            var proxPartido = equipoRealServicio.proximoPartido(nombreEquipo)
            model["proxPartido"] = proxPartido

            if(proxPartido.equipoVisitante?.name == equipo.name) {
                model["rival"] = proxPartido.equipoLocal!!
            }else {
                model["rival"] = proxPartido.equipoVisitante!!
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

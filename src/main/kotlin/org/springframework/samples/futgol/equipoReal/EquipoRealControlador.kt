package org.springframework.samples.futgol.equipoReal

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import java.util.stream.Collectors

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
        model["equiposReales"] = equipoRealServicio.buscarTodosEquiposReales()!!.sortedBy { x -> x.posicion }
        return VISTA_LISTA_EQUIPOSREALES
    }

    @GetMapping("/equiposLiga/{nombreEquipo}")
    fun detallesEquipoReal(model: Model, @PathVariable("nombreEquipo") nombreEquipo: String): String {
        var equipo = equipoRealServicio.buscarEquipoRealPorNombre(nombreEquipo)!!
        model["equipo"] = equipo
        var jugadores = equipo.jugadores
        var portero = equipo.jugadores.stream()?.filter { x -> x?.posicion == "PO" }.filter{x->x.estadoLesion=="En forma"}
            .sorted(Comparator.comparing { x -> -x.valor })?.findFirst()?.get()
        model["portero"] = portero!!
        var defensas = equipo.jugadores.stream()?.filter { x -> x?.posicion == "DF" }.filter{x->x.estadoLesion=="En forma"}
            .sorted(Comparator.comparing { x -> -x.valor })
            ?.limit(4)
            ?.collect(Collectors.toList())
        model["defensas"] = defensas!!
        var centrocampistas = equipo.jugadores.stream()?.filter { x -> x?.posicion == "CC" }.filter{x->x.estadoLesion=="En forma"}
            .sorted(Comparator.comparing { x -> -x.valor })
            ?.limit(4)
            ?.collect(Collectors.toList())
        model["centrocampistas"] = centrocampistas!!
        var delanteros = equipo.jugadores.stream()?.filter { x -> x?.posicion == "DL" }.filter{x->x.estadoLesion=="En forma"}
            .sorted(Comparator.comparing { x -> -x.valor })
            ?.limit(2)
            ?.collect(Collectors.toList())
        model["delanteros"] = delanteros!!
        model["partidosLocal"] = equipo.partidosLocal.sortedBy { x -> x.jornada?.numeroJornada }
        model["partidosVisitante"] = equipo.partidosVisitante.sortedBy { x -> x.jornada?.numeroJornada }
        model["jugadores"] = jugadores
        if (jugadores.size > 5) {
            model["top5Jugadores"] = equipo.jugadores.sortedBy { x -> -x.puntos }.subList(0, 5)
        }
        return VISTA_DETALLES_EQUIPOREAL
    }
}

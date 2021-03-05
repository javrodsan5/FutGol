package org.springframework.samples.futgol.jugador

import org.springframework.samples.futgol.clausula.Clausula
import org.springframework.samples.futgol.clausula.ClausulaServicio
import org.springframework.samples.futgol.clausula.ClausulaValidador
import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.equipoReal.EquipoRealServicio
import org.springframework.samples.futgol.usuario.UsuarioServicio
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import java.security.Principal
import java.util.stream.Collectors
import javax.validation.Valid


@Controller
class JugadorControlador(
    val jugadorServicio: JugadorServicio,
    val equipoServicio: EquipoServicio, val equipoRealServicio: EquipoRealServicio,
    val clausulaServicio: ClausulaServicio, val usuarioServicio: UsuarioServicio
) {

    private val VISTA_WSJUGADORES = "jugadores/wsJugadores"
    private val VISTA_DETALLES_JUGADOR = "jugadores/detallesJugador"
    private val VISTA_CLAUSULA_JUGADOR = "jugadores/clausulaJugador"
    private val VISTA_DETALLES_JUGADOR_EQUIPO = "jugadores/detallesJugadorEquipo"
    private val VISTA_BUSCAR_JUGADOR = "jugadores/buscaJugador"

    @InitBinder("clausula")
    fun initClausulaBinder(dataBinder: WebDataBinder) {
        dataBinder.validator = ClausulaValidador()
    }

    @GetMapping("/WSJugadores")
    fun iniciaWSJugadores(model: Model): String {
        return VISTA_WSJUGADORES
    }

    @PostMapping("/WSJugadores")
    fun creaWSJugadores(model: Model): String {
        //this.jugadorServicio.webScrapingJugadoresTransfermarkt()
        this.jugadorServicio.webScrapingJugadoresFbref()
        return VISTA_WSJUGADORES
    }

    @GetMapping("/jugador/{idJugador}")
    fun detallesJugadorCualquiera(model: Model, @PathVariable("idJugador") idJugador: Int): String {
        if (jugadorServicio.existeJugadorId(idJugador) == true) {
            model["jugador"] = jugadorServicio.buscaJugadorPorId(idJugador)!!
        } else {
            return "redirect:/"
        }
        return VISTA_DETALLES_JUGADOR
    }

    @GetMapping("/topJugadores")
    fun buscaJugador(model: Model): String {
        model["jugadores"] =
            jugadorServicio.buscaTodosJugadores()?.stream()?.sorted(Comparator.comparing { x -> -x.puntos })?.limit(5)
                ?.collect(Collectors.toList())!!
        model.addAttribute(Jugador())
        return VISTA_BUSCAR_JUGADOR
    }

    @GetMapping("/jugadores")
    fun procesoBusquedaJugador(jugador: Jugador, result: BindingResult, model: Model): String {
        return if (jugador.name?.let { jugadorServicio.existeJugadorNombre(it) } == true) {
            var jug = jugadorServicio.buscaJugadorPorNombre(jugador.name!!)
            "redirect:/jugador/" + jug!!.id
        } else {
            result.rejectValue("name", "no se ha encontrado", "no se ha encontrado")
            model["jugadores"] =
                jugadorServicio.buscaTodosJugadores()?.stream()?.sorted(Comparator.comparing { x -> -x.puntos })
                    ?.limit(5)
                    ?.collect(Collectors.toList())!!
            VISTA_BUSCAR_JUGADOR
        }
    }

    @GetMapping("/equipo/{idEquipo}/jugador/{idJugador}")
    fun detallesJugadorEquipo(
        model: Model,
        @PathVariable("idEquipo") idEquipo: Int,
        @PathVariable("idJugador") idJugador: Int, principal: Principal?
    ): String {
        var equipo = equipoServicio.buscaEquiposPorId(idEquipo)!!
        if (jugadorServicio.checkJugadorEnEquipo(idJugador, idEquipo)) {
            model["jugador"] = jugadorServicio.buscaJugadorPorId(idJugador)!!
            model["equipo"] = equipo
            model["clausula"] = clausulaServicio.buscarClausulasPorJugadorYEquipo(idJugador, idEquipo)!!
            if (equipo!!.usuario?.user?.username == principal?.let { usuarioServicio.usuarioLogueado(it)?.user?.username }) {
                model["loTengoEnMiEquipo"] = true
            }
        } else {
            return "redirect:/"
        }
        return VISTA_DETALLES_JUGADOR_EQUIPO
    }

    @GetMapping("/equipo/{idEquipo}/jugador/{idJugador}/clausula")
    fun clausulaJugador(
        model: Model,
        @PathVariable("idJugador") idJugador: Int,
        @PathVariable("idEquipo") idEquipo: Int, principal: Principal?
    ): String {
        var equipo = equipoServicio.buscaEquiposPorId(idEquipo)
        var usuario = principal?.let { usuarioServicio.usuarioLogueado(it) }
        if (usuario != null) {
            if (equipo!!.usuario?.user?.username == usuario.user?.username) {
                if (jugadorServicio.checkJugadorEnEquipo(idJugador, idEquipo)) {
                    model["clausula"] = clausulaServicio.buscarClausulasPorJugadorYEquipo(idJugador, idEquipo)!!
                }
            } else {
                return "redirect:/jugador/" + idJugador
            }
        }
        return VISTA_CLAUSULA_JUGADOR
    }

    @PostMapping("/equipo/{idEquipo}/jugador/{idJugador}/clausula")
    fun actualizarClausulaJugador(
        @Valid clausula: Clausula,
        result: BindingResult,
        model: Model,
        @PathVariable("idEquipo") idEquipo: Int,
        @PathVariable("idJugador") idJugador: Int
    ): String {

        if (result.hasErrors()) {
            model["clausula"] = clausula
            return VISTA_CLAUSULA_JUGADOR
        } else {
            clausula.equipo = equipoServicio.buscaEquiposPorId(idEquipo)
            clausula.jugador = jugadorServicio.buscaJugadorPorId(idJugador)
            clausulaServicio.guardarClausula(clausula)
        }
        return "redirect:/equipo/$idEquipo/jugador/" + idJugador
    }
}

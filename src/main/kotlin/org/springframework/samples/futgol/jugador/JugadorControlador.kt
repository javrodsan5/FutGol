package org.springframework.samples.futgol.jugador

import org.springframework.cache.annotation.CachePut
import org.springframework.samples.futgol.clausula.Clausula
import org.springframework.samples.futgol.clausula.ClausulaServicio
import org.springframework.samples.futgol.clausula.ClausulaValidador
import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.equipoReal.EquipoRealServicio
import org.springframework.samples.futgol.estadisticaJugador.EstadisticaJugadorServicio
import org.springframework.samples.futgol.jornadas.JornadaServicio
import org.springframework.samples.futgol.liga.LigaServicio
import org.springframework.samples.futgol.usuario.UsuarioServicio
import org.springframework.samples.futgol.util.MetodosAux
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
import java.util.*
import java.util.stream.Collectors
import javax.validation.Valid
import kotlin.Comparator


@Controller
class JugadorControlador(
    val jugadorServicio: JugadorServicio,
    val equipoServicio: EquipoServicio,
    val equipoRealServicio: EquipoRealServicio,
    val clausulaServicio: ClausulaServicio,
    val estadisticaJugadorServicio: EstadisticaJugadorServicio,
    val usuarioServicio: UsuarioServicio,
    val jornadaServicio: JornadaServicio,
    val ligaServicio: LigaServicio
) {

    private val VISTA_DETALLES_JUGADOR = "jugadores/detallesJugador"
    private val VISTA_CLAUSULA_JUGADOR = "jugadores/clausulaJugador"
    private val VISTA_BUSCAR_JUGADOR = "jugadores/buscaJugador"

    @InitBinder("clausula")
    fun initClausulaBinder(dataBinder: WebDataBinder) {
        dataBinder.validator = ClausulaValidador()
    }

    @CachePut("detallesJugador")
    @GetMapping("/jugador/{idJugador}/jornada/{numeroJornada}")
    fun detallesJugadorCualquiera(
        model: Model,
        @PathVariable idJugador: Int,
        @PathVariable numeroJornada: Int
    ): String {
        return if (jugadorServicio.existeJugadorId(idJugador) == true && jornadaServicio.existeJornada(numeroJornada) == true) {
            val jugador = jugadorServicio.buscaJugadorPorId(idJugador)!!
            model["jugador"] = jugador
            var mediasJug = jugadorServicio.mediaEstadisticasJugador(idJugador)!!
            if (mediasJug.isNotEmpty()) {
                model["tieneMedias"] = true
                model["medias"] = mediasJug
            }
            model["esPortero"] = jugador.posicion == "PO"
            model["esCCoDL"] = jugador.posicion == "CC" || jugador.posicion == "DL"
            model["esDF"] = jugador.posicion == "DF"
            if (jugador.id?.let {
                    this.estadisticaJugadorServicio.existeEstadisticaJugadorJornada(
                        it,
                        numeroJornada
                    )
                } == true) {
                model["tieneEstadistica"] = true
                model["est"] =
                    estadisticaJugadorServicio.buscarEstadisticasPorJugadorJornada(idJugador, numeroJornada)!!
            } else {
                model["tieneEstadistica"] = false
            }
            model["equipoNoReal"] = false
            model["jornadas"] = jornadaServicio.buscarTodasJornadas()!!
            VISTA_DETALLES_JUGADOR
        } else {
            "redirect:/"
        }
    }

    @GetMapping("/topJugadores")
    fun buscaJugador(model: Model): String {
        model["jugadores"] =
            jugadorServicio.buscaJugadoresOrdenPuntos()?.stream()?.limit(5)
                ?.collect(Collectors.toList())!!
        model.addAttribute(Jugador())
        return VISTA_BUSCAR_JUGADOR
    }

    @GetMapping("/jugadores")
    fun procesoBusquedaJugador(jugador: Jugador, result: BindingResult, model: Model): String {
        return if (jugador.name?.let { jugadorServicio.existeJugadorNombre(it) } == true) {
            var jug = jugadorServicio.buscaJugadorPorNombre(jugador.name!!)
            "redirect:/jugador/" + jug!!.id + "/jornada/1"
        } else {
            result.rejectValue("name", "No se ha encontrado", "No se ha encontrado")
            model["jugadores"] =
                jugadorServicio.buscaTodosJugadores()?.stream()?.sorted(Comparator.comparing { x -> -x.puntos })
                    ?.limit(5)
                    ?.collect(Collectors.toList())!!
            VISTA_BUSCAR_JUGADOR
        }
    }

    @GetMapping("/equipo/{idEquipo}/jugador/{idJugador}/jornada/{numeroJornada}")
    fun detallesJugadorEquipo(
        model: Model,
        @PathVariable idEquipo: Int,
        @PathVariable idJugador: Int, @PathVariable numeroJornada: Int, principal: Principal?
    ): String {
        if (equipoServicio.comprobarSiExisteEquipo(idEquipo) == true && jugadorServicio.existeJugadorEnEquipo(
                idJugador, idEquipo
            ) && jornadaServicio.existeJornada(numeroJornada) == true
        ) {
            model["equipoNoReal"] = true
            var equipo = equipoServicio.buscaEquiposPorId(idEquipo)!!
            var jugador = jugadorServicio.buscaJugadorPorId(idJugador)!!
            model["jugador"] = jugador
            val clausula = this.clausulaServicio.buscarClausulasPorJugadorYEquipo(idJugador, idEquipo)!!
            model["clausula"] = MetodosAux().enteroAEuros(clausula.valorClausula)

            if((Date().time - clausula.ultModificacion.time) / 86400000 >= 8) {
                model["puedeActualizarClausula"] = true
            }

            if (jugadorServicio.tieneEstadisticas(idJugador) == true) {
                var mediasJug = jugadorServicio.mediaEstadisticasJugador(idJugador)!!
                model["tieneMedias"] = true
                model["medias"] = mediasJug
                model["esCCoDL"] = jugador.posicion == "CC" || jugador.posicion == "DL"
                model["esDF"] = jugador.posicion == "DF"
            }
            model["esPortero"] = jugador.posicion == "PO"

            if (jugador.id?.let {
                    this.estadisticaJugadorServicio.existeEstadisticaJugadorJornada(
                        it,
                        numeroJornada
                    )
                } == true) {
                model["tieneEstadistica"] = true
                model["est"] =
                    estadisticaJugadorServicio.buscarEstadisticasPorJugadorJornada(idJugador, numeroJornada)!!
            } else {

                model["tieneEstadistica"] = false
            }
            model["jornadas"] = jornadaServicio.buscarTodasJornadas()!!
            model["equipo"] = equipo
            model["liga"] = equipo.liga!!

            if (equipo.usuario?.user?.username == principal?.let { usuarioServicio.usuarioLogueado(it)?.user?.username }) {
                model["loTengoEnMiEquipo"] = true
            }else {
                model["noLoTengoEnMiEquipo"] = true
            }
        } else {
            return "redirect:/"
        }
        return VISTA_DETALLES_JUGADOR
    }

    @GetMapping("/equipo/{idEquipo}/jugador/{idJugador}/clausula")
    fun iniciarActualizacionClausulaJugador(
        model: Model,
        @PathVariable("idJugador") idJugador: Int,
        @PathVariable("idEquipo") idEquipo: Int, principal: Principal?
    ): String {
        if (equipoServicio.comprobarSiExisteEquipo(idEquipo) == true && jugadorServicio.existeJugadorEnEquipo(
                idJugador, idEquipo
            )
        ) {
            model["valido"] = true
            var clausula = clausulaServicio.buscarClausulasPorJugadorYEquipo(idJugador, idEquipo)!!
            if ((Date().time - clausula.ultModificacion.time) / 86400000 >= 8) {
                var usuario = principal?.let { usuarioServicio.usuarioLogueado(it) }
                if (usuario != null && equipoServicio.buscaEquiposPorId(idEquipo)!!.usuario?.user?.username == usuario.user?.username) {
                    model["clausula"] = clausulaServicio.buscarClausulasPorJugadorYEquipo(idJugador, idEquipo)!!
                    return VISTA_CLAUSULA_JUGADOR
                }
                return "redirect:/equipo/$idEquipo/jugador/$idJugador/jornada/1"
            }
            return "redirect:/equipo/$idEquipo/jugador/$idJugador/jornada/1"
        } else {
            return "redirect:/equipo/$idEquipo/jugador/$idJugador/jornada/1"
        }
        return VISTA_CLAUSULA_JUGADOR
    }

    @PostMapping("/equipo/{idEquipo}/jugador/{idJugador}/clausula")
    fun procesarActualizacionClausulaJugador(
        @Valid clausula: Clausula,
        result: BindingResult,
        model: Model,
        @PathVariable("idEquipo") idEquipo: Int,
        @PathVariable("idJugador") idJugador: Int
    ): String {

        if (result.hasErrors()) {
            model["valido"] = false
            model["clausula"] = clausula
            return VISTA_CLAUSULA_JUGADOR
        } else {
            val j = jugadorServicio.buscaJugadorPorId(idJugador)
            return if (clausula.valorClausula < (j?.valor?.times(1000000)!!)) {
                model["valido"] = false
                result.rejectValue(
                    "valorClausula",
                    "La cláusula no puede ser inferior al valor del jugador",
                    "La cláusula no puede ser inferior al valor del jugador"
                )
                VISTA_CLAUSULA_JUGADOR
            } else {
                model["valido"] = true
                clausula.ultModificacion = Date()
                clausula.equipo = equipoServicio.buscaEquiposPorId(idEquipo)
                clausula.jugador = j
                clausulaServicio.guardarClausula(clausula)
                return "redirect:/equipo/$idEquipo/jugador/" + idJugador + "/jornada/1"
            }
        }
    }

    @GetMapping("/liga/{idLiga}/equipo/{idEquipo}/jugador/{idJugador}/pagarClausula")
    fun pagarClausula(
        model: Model,
        @PathVariable("idJugador") idJugador: Int, @PathVariable("idLiga") idLiga: Int,
        @PathVariable("idEquipo") idEquipo: Int, principal: Principal): String {

        if (ligaServicio.estoyEnLiga2(idLiga, principal) && equipoServicio.comprobarSiExisteEquipoLiga2(idEquipo, idLiga) && jugadorServicio.existeJugadorEnEquipo(
                idJugador, idEquipo) && equipoServicio.tengoEquipo(idLiga, principal)) {
            val miEquipo = equipoServicio.buscaMiEquipoEnLiga(idLiga, principal)!!
            if(miEquipo.id!=idEquipo) {
                var otroEquipo = equipoServicio.buscaEquiposPorId(idEquipo)!!
                val jugador = jugadorServicio.buscaJugadorPorId(idJugador)!!
                var clausula = clausulaServicio.buscarClausulasPorJugadorYEquipo(idJugador, idEquipo)!!
                miEquipo.dineroRestante-= clausula.valorClausula
                miEquipo.jugadores.add(jugador)
                miEquipo.jugBanquillo.add(jugador)

                otroEquipo.dineroRestante+= clausula.valorClausula
                otroEquipo.jugBanquillo.removeIf { x -> x.id==idJugador }
                otroEquipo.jugadores.removeIf { x -> x.id==idJugador }
                otroEquipo.onceInicial.removeIf { x -> x.id==idJugador }

                equipoServicio.guardarEquipo(miEquipo)
                equipoServicio.guardarEquipo(otroEquipo)
                clausulaServicio.borraClausulaByEquipoIdAndJugadorId(idEquipo, idJugador)

                var clau = Clausula()
                clau.equipo = miEquipo
                clau.jugador = jugador
                var fecha = Calendar.getInstance()
                fecha.add(Calendar.DAY_OF_YEAR, -9)
                clau.ultModificacion= fecha.time
                clau.valorClausula = ((jugador.valor + (jugador.valor * 0.5))*1000000).toInt()
                this.clausulaServicio.guardarClausula(clau)
            }
        }
        return "redirect:/liga/$idLiga/miEquipo"
    }
}

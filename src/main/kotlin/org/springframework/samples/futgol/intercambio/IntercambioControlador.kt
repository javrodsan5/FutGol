package org.springframework.samples.futgol.intercambio

import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.jugador.JugadorServicio
import org.springframework.samples.futgol.liga.LigaServicio
import org.springframework.samples.futgol.usuario.UsuarioServicio
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import java.security.Principal

@Controller
class IntercambioControlador(
    val intercambioServicio: IntercambioServicio,
    val ligaServicio: LigaServicio,
    val equipoServicio: EquipoServicio,
    val jugadorServicio: JugadorServicio,
    val usuarioServicio: UsuarioServicio
) {

    private val VISTA_INTERCAMBIOS = "equipos/intercambios"
    private val VISTA_CREAR_INTERCAMBIOS = "equipos/crearIntercambio"


    @GetMapping("liga/{idLiga}/misIntercambios")
    fun intercambiosEquipo(
        model: Model, @PathVariable idLiga: Int, principal: Principal
    ): String {
        if (ligaServicio.comprobarSiExisteLiga2(idLiga) == true && ligaServicio.estoyEnLiga2(
                idLiga, principal
            ) && equipoServicio.tengoEquipo(idLiga, principal)
        ) {
            model["liga"] = ligaServicio.buscarLigaPorId(idLiga)!!
            val miEquipoId = equipoServicio.buscaMiEquipoEnLiga(idLiga, principal).id!!
            if (intercambioServicio.existenIntercambiosEquipo(miEquipoId) == true) {
                model["tienesInt"] = true
                model["intercambios"] = intercambioServicio.buscarIntercambiosEquipo(miEquipoId)!!
            } else {
                model["tienesInt"] = false
            }
            return VISTA_INTERCAMBIOS
        }
        return "redirect:/misligas"
    }

    @GetMapping("liga/{idLiga}/equipos/{idEquipo}/intercambio/{idIntercambio}/aceptar")
    fun aceptarIntercambio(
        model: Model, @PathVariable idLiga: Int, principal: Principal,
        @PathVariable idEquipo: Int, @PathVariable idIntercambio: Int
    ): String {
        val usuario = usuarioServicio.usuarioLogueado(principal)!!
        if (ligaServicio.comprobarSiExisteLiga2(idLiga) == true && ligaServicio.estoyEnLiga2(idLiga, principal)
            && equipoServicio.tengoEquipo(idLiga, principal) &&
            intercambioServicio.existeIntercambioPorIdIntercIdEquipo(idIntercambio, idEquipo)
            == true
        ) {
            var intercambio = intercambioServicio.buscaIntercambioPorId(idIntercambio)!!
            var otroEquipo = intercambio.otroEquipo!!
            otroEquipo.jugadores?.add(intercambio.jugadorCreadorIntercambio!!)
            otroEquipo.jugBanquillo?.add(intercambio.jugadorCreadorIntercambio!!)
            otroEquipo.dineroRestante = intercambio.otroEquipo?.dineroRestante?.plus(intercambio.dinero)!!
            otroEquipo.jugadores?.removeIf { x -> x.id == intercambio.otroJugador?.id }
            otroEquipo.jugBanquillo?.removeIf { x -> x.id == intercambio.otroJugador?.id }
            equipoServicio.guardarEquipo(otroEquipo)

            var equipoCreadorIntercambio = intercambio.equipoCreadorIntercambio!!
            equipoCreadorIntercambio.jugadores?.add(intercambio.otroJugador!!)
            equipoCreadorIntercambio.jugBanquillo?.add(intercambio.otroJugador!!)
            equipoCreadorIntercambio.jugadores?.removeIf { x -> x.id == intercambio.jugadorCreadorIntercambio?.id }
            equipoCreadorIntercambio.jugBanquillo?.removeIf { x -> x.id == intercambio.jugadorCreadorIntercambio?.id }
            equipoCreadorIntercambio.dineroRestante =
                intercambio.equipoCreadorIntercambio?.dineroRestante?.minus(intercambio.dinero)!!
            equipoServicio.guardarEquipo(equipoCreadorIntercambio)

            intercambioServicio.borraIntercambio(idIntercambio)
        }
        return "redirect:/liga/$idLiga/misIntercambios"
    }

    @GetMapping("liga/{idLiga}/equipos/{idEquipo}/intercambio/{idIntercambio}/rechazar")
    fun rechazarIntercambio(
        model: Model, @PathVariable idLiga: Int, principal: Principal,
        @PathVariable idEquipo: Int, @PathVariable idIntercambio: Int
    ): String {
        if (ligaServicio.comprobarSiExisteLiga2(idLiga) == true && ligaServicio.estoyEnLiga2(idLiga, principal)
            && equipoServicio.tengoEquipo(idLiga, principal) &&
            intercambioServicio.existeIntercambioPorIdIntercIdEquipo(idIntercambio, idEquipo) == true
        ) {
            intercambioServicio.borraIntercambio(idIntercambio)
        }
        return "redirect:/liga/$idLiga/misIntercambios"
    }

    @GetMapping("liga/{idLiga}/nuevoIntercambio/{idJugador}")
    fun initOfrecerIntercambio(
        model: Model, @PathVariable idLiga: Int, principal: Principal, @PathVariable idJugador: Int
    ): String {

        if (ligaServicio.comprobarSiExisteLiga2(idLiga) == true && ligaServicio.estoyEnLiga2(idLiga, principal)
            && equipoServicio.tengoEquipo(idLiga, principal) && jugadorServicio.existeJugadorId(idJugador) == true
            && equipoServicio.estaJugadorEnEquiposLiga(idLiga, idJugador)
        ) {
            val miEquipo = equipoServicio.buscaMiEquipoEnLiga(idLiga, principal)
            if (!jugadorServicio.existeJugadorEnEquipo(idJugador, miEquipo.id!!)) {
                model["jugador"] = jugadorServicio.buscaJugadorPorId(idJugador)!!
                model["intercambio"] = Intercambio()
                model["liga"] = ligaServicio.buscarLigaPorId(idLiga)!!
                model["misJugadores"] = miEquipo.jugadores
            }
        }

        return VISTA_CREAR_INTERCAMBIOS
    }

    @PostMapping("liga/{idLiga}/nuevoIntercambio/{idJugador}")
    fun postOfrecerIntercambio(
        intercambio: Intercambio, result: BindingResult, model: Model, @PathVariable idLiga: Int,
        principal: Principal, @PathVariable idJugador: Int
    ): String {

        return if (result.hasErrors()) {
            model["intercambio"] = intercambio
            VISTA_CREAR_INTERCAMBIOS
        } else {
            intercambio.equipoCreadorIntercambio = equipoServicio.buscaMiEquipoEnLiga(idLiga, principal)

            intercambio.otroJugador = jugadorServicio.buscaJugadorPorId(idJugador)!!
            intercambio.otroEquipo = equipoServicio.buscarEquipoLigaJugador(idLiga, idJugador)
            intercambioServicio.guardarIntercambio(intercambio)
            return "redirect:/liga/$idLiga/miEquipo"
        }
    }


}

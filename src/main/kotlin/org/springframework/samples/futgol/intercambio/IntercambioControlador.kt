package org.springframework.samples.futgol.intercambio

import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.jugador.JugadorServicio
import org.springframework.samples.futgol.liga.LigaServicio
import org.springframework.samples.futgol.movimientos.Movimiento
import org.springframework.samples.futgol.movimientos.MovimientoServicio
import org.springframework.samples.futgol.subasta.SubastaServicio
import org.springframework.samples.futgol.usuario.UsuarioServicio
import org.springframework.samples.futgol.util.MetodosAux
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
    val usuarioServicio: UsuarioServicio,
    val subastaServicio: SubastaServicio,
    val movimientoServicio: MovimientoServicio
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
                model["miEquipoId"] = miEquipoId
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
        if (ligaServicio.comprobarSiExisteLiga2(idLiga) == true && ligaServicio.estoyEnLiga2(idLiga, principal)
            && equipoServicio.tengoEquipo(idLiga, principal) &&
            intercambioServicio.existeIntercambioPorIdIntercIdEquipo(idIntercambio, idEquipo)
            == true
        ) {
            var intercambio = intercambioServicio.buscaIntercambioPorId(idIntercambio)!!
            var otroEquipo = intercambio.otroEquipo!!
            otroEquipo.jugadores.add(intercambio.jugadorCreadorIntercambio!!)
            otroEquipo.jugBanquillo.add(intercambio.jugadorCreadorIntercambio!!)
            otroEquipo.dineroRestante = intercambio.otroEquipo?.dineroRestante?.plus(intercambio.dinero)!!
            otroEquipo.jugadores.removeIf { x -> x.id == intercambio.otroJugador?.id }
            otroEquipo.jugBanquillo.removeIf { x -> x.id == intercambio.otroJugador?.id }
            equipoServicio.guardarEquipo(otroEquipo)

            var equipoCreadorIntercambio = intercambio.equipoCreadorIntercambio!!
            equipoCreadorIntercambio.jugadores.add(intercambio.otroJugador!!)
            equipoCreadorIntercambio.jugBanquillo.add(intercambio.otroJugador!!)
            equipoCreadorIntercambio.jugadores.removeIf { x -> x.id == intercambio.jugadorCreadorIntercambio?.id }
            equipoCreadorIntercambio.jugBanquillo.removeIf { x -> x.id == intercambio.jugadorCreadorIntercambio?.id }
            equipoCreadorIntercambio.dineroRestante =
                intercambio.equipoCreadorIntercambio?.dineroRestante?.minus(intercambio.dinero)!!
            equipoServicio.guardarEquipo(equipoCreadorIntercambio)
            var movimiento = Movimiento()
            movimiento.jugador = intercambio.jugadorCreadorIntercambio
            movimiento.jugador2 = intercambio.otroJugador
            movimiento.creadorMovimiento = intercambio.equipoCreadorIntercambio?.usuario
            movimiento.creadorMovimiento2 = intercambio.otroEquipo?.usuario
            movimiento.liga = this.ligaServicio.buscarLigaPorId(idLiga)
            movimiento.texto = movimiento.creadorMovimiento?.user?.username + " ha intercambiado con " +
                    movimiento.creadorMovimiento2?.user?.username + " al jugador " +
                    movimiento.jugador?.name + " por el jugador " + movimiento.jugador2?.name + " y una diferencia de " +
                    MetodosAux().enteroAEuros(intercambio.dinero) + "."
            movimiento.textoPropio = "Has intercambiado con " +
                    movimiento.creadorMovimiento2?.user?.username + " al jugador " +
                    movimiento.jugador?.name + " por el jugador " + movimiento.jugador2?.name + " y una diferencia de " +
                    MetodosAux().enteroAEuros(intercambio.dinero) + "."

            var movimiento2 = Movimiento()
            movimiento2.jugador = intercambio.otroJugador
            movimiento2.jugador2 = intercambio.jugadorCreadorIntercambio
            movimiento2.creadorMovimiento = intercambio.otroEquipo?.usuario
            movimiento2.creadorMovimiento2 = intercambio.equipoCreadorIntercambio?.usuario
            movimiento2.liga = this.ligaServicio.buscarLigaPorId(idLiga)
            movimiento2.texto = movimiento2.creadorMovimiento?.user?.username + " ha intercambiado con " +
                    movimiento2.creadorMovimiento2?.user?.username + " al jugador " +
                    movimiento2.jugador?.name + " por el jugador " + movimiento2.jugador2?.name + " y una diferencia de " +
                    MetodosAux().enteroAEuros(intercambio.dinero) + "."
            movimiento2.textoPropio = "Has intercambiado con " +
                    movimiento2.creadorMovimiento2?.user?.username + " al jugador " +
                    movimiento2.jugador?.name + " por el jugador " + movimiento2.jugador2?.name + " y una diferencia de " +
                    MetodosAux().enteroAEuros(intercambio.dinero) + "."

            this.movimientoServicio.guardarMovimiento(movimiento)
            this.movimientoServicio.guardarMovimiento(movimiento2)

            var intercambiosotroEquipo = intercambioServicio.buscarIntercambiosEquipo(otroEquipo.id!!)!!
            for (i in intercambiosotroEquipo) {
                if (i.id != intercambio.id && i.otroJugador!!.id == intercambio.otroJugador!!.id) {
                    intercambioServicio.borraIntercambio(i.id!!)
                }
            }
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
            && equipoServicio.estaJugadorEnEquiposLiga(idLiga, idJugador) && subastaServicio.buscarSubastaPorLigaId(
                idLiga
            )?.jugadores?.none { j -> j.id == idJugador } == true
        ) {
            val miEquipo = equipoServicio.buscaMiEquipoEnLiga(idLiga, principal)
            if (!jugadorServicio.existeJugadorEnEquipo(idJugador, miEquipo.id!!)) {
                model["jugador"] = jugadorServicio.buscaJugadorPorId(idJugador)!!
                model["dineroRestante"] = miEquipo.dineroRestante
                model["intercambio"] = Intercambio()
                model["liga"] = ligaServicio.buscarLigaPorId(idLiga)!!
                model["misJugadores"] = miEquipo.jugadores
                return VISTA_CREAR_INTERCAMBIOS
            }
        }
        return "redirect:/misligas"

    }

    @PostMapping("liga/{idLiga}/nuevoIntercambio/{idJugador}")
    fun postOfrecerIntercambio(
        intercambio: Intercambio, result: BindingResult, model: Model, @PathVariable idLiga: Int,
        principal: Principal, @PathVariable idJugador: Int
    ): String {

        intercambio.equipoCreadorIntercambio = equipoServicio.buscaMiEquipoEnLiga(idLiga, principal)
        intercambio.otroJugador = jugadorServicio.buscaJugadorPorId(idJugador)!!
        intercambio.otroEquipo = equipoServicio.buscarEquipoLigaJugador(idLiga, idJugador)
        intercambioServicio.guardarIntercambio(intercambio)
        return "redirect:/liga/$idLiga/miEquipo"
    }


}

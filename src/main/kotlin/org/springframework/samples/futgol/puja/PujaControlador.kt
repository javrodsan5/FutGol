package org.springframework.samples.futgol.puja

import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.jugador.JugadorServicio
import org.springframework.samples.futgol.liga.LigaServicio
import org.springframework.samples.futgol.subasta.SubastaServicio
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

@Controller
class PujaControlador(
    val pujaServicio: PujaServicio,
    val ligaServicio: LigaServicio,
    val jugadorServicio: JugadorServicio,
    val equipoServicio: EquipoServicio, val subastaServicio: SubastaServicio
) {
    private val VISTA_PUJA = "pujas/puja"

    @GetMapping("/liga/{idLiga}/subastas/{idJugador}")
    fun iniciarPujaJugador(
        @PathVariable idLiga: Int,
        @PathVariable idJugador: Int,
        model: Model,
        principal: Principal
    ): String {
        if (ligaServicio.comprobarSiExisteLiga2(idLiga) == true && ligaServicio.estoyEnLiga2(
                idLiga, principal
            ) && jugadorServicio.existeJugadorId(idJugador) == true && equipoServicio.tengoEquipo(
                idLiga, principal
            ) && subastaServicio.existeSubastaPorLigaId(idLiga) == true
        ) {
            val miEquipo = equipoServicio.buscaMiEquipoEnLiga(idLiga, principal)
            model["valido"] = true
            if (miEquipo.jugadores.size < 24) {
                val subasta = subastaServicio.buscarSubastaPorLigaId(idLiga)
                val jugador = jugadorServicio.buscaJugadorPorId(idJugador)!!
                if (!miEquipo.id?.let {
                        jugadorServicio.existeJugadorEnEquipo(idJugador, it)
                    }!! && jugador in subasta!!.jugadores) {
                    model["equipo"] = miEquipo
                    model["jugador"] = jugador
                    model["numPujas"] =
                        subasta.id?.let { pujaServicio.buscarPujasJugadorSubasta(idJugador, it)?.size }!!
                    model["puja"] = Puja()
                    model["liga"] = ligaServicio.buscarLigaPorId(idLiga)!!
                    model["dineroRestante"] = MetodosAux().enteroAEuros((miEquipo.dineroRestante))
                    return VISTA_PUJA
                }
            }
        } else {
            return "redirect:/misligas"
        }
        return "redirect:/liga/$idLiga/subastas"

    }

    @PostMapping("/liga/{idLiga}/subastas/{idJugador}")
    fun pujarJugador(
        puja: Puja, result: BindingResult, @PathVariable idLiga: Int,
        @PathVariable idJugador: Int, model: Model, principal: Principal
    ): String {
        val subasta = subastaServicio.buscarSubastaPorLigaId(idLiga)!!
        val equipo = equipoServicio.buscaMiEquipoEnLiga(idLiga, principal)
        val jugador = jugadorServicio.buscaJugadorPorId(idJugador)
        model["equipo"] = equipo
        model["jugador"] = jugador!!
        model["subasta"] = subasta
        model["valido"] = true

        if (subasta.id?.let { pujaServicio.existePujaEqJugSub(equipo.id!!, idJugador, it) } == true) {
            pujaServicio.borraPujaByEquipoJugadorSubasta(equipo.id!!, idJugador, subasta.id!!)
        }
        return if (puja.cantidad > equipo.dineroRestante || puja.cantidad < jugador.valor || puja.cantidad <= 0) {
            model["valido"] = false
            model["puja"] = puja
            model["numPujas"] =
                subasta.id?.let { pujaServicio.buscarPujasJugadorSubasta(idJugador, it)?.size }!!
            model["dineroRestante"] = MetodosAux().enteroAEuros((equipo.dineroRestante))
            model["liga"] = ligaServicio.buscarLigaPorId(idLiga)!!
            var mensaje = ""
            when {
                puja.cantidad > equipo.dineroRestante -> mensaje =
                    "No tienes saldo suficiente para poder realizar la puja."
                puja.cantidad <= 0 -> mensaje = "Tu puja no puede ser igual o inferior a 0€."
                puja.cantidad < jugador.valor -> mensaje = "No puedes realizar una puja inferior al valor del jugador."
            }
            result.rejectValue("cantidad", mensaje, mensaje)
            VISTA_PUJA
        } else {
            puja.equipo = equipo
            puja.subasta = subasta
            puja.jugador = jugadorServicio.buscaJugadorPorId(idJugador)
            pujaServicio.guardarPuja(puja)
            "redirect:/liga/$idLiga/subastas"
        }
    }

}

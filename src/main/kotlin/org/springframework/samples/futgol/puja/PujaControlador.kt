package org.springframework.samples.futgol.puja

import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.jugador.JugadorServicio
import org.springframework.samples.futgol.liga.LigaServicio
import org.springframework.samples.futgol.subasta.SubastaServicio
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

    @InitBinder("puja")
    fun initPujaBinder(dataBinder: WebDataBinder) {
        dataBinder.validator = PujaValidador()
    }

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
            println(miEquipo.jugadores.size)
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
        val liga = ligaServicio.buscarLigaPorId(idLiga)!!
        if (result.hasErrors()) {
            model["liga"] = liga
            model["equipo"] = equipoServicio.buscaMiEquipoEnLiga(idLiga, principal)
            model["puja"] = puja
            return VISTA_PUJA
        }
        val subasta = subastaServicio.buscarSubastaPorLigaId(idLiga)!!
        val equipo = equipoServicio.buscaMiEquipoEnLiga(idLiga, principal)
        if (subasta.id?.let { pujaServicio.existePujaEqJugSub(equipo.id!!, idJugador, it) } == true) {
            pujaServicio.borraPujaByEquipoJugadorSubasta(equipo.id!!, idJugador, subasta.id!!)
        }
        puja.equipo = equipo
        puja.subasta = subasta
        puja.jugador = jugadorServicio.buscaJugadorPorId(idJugador)
        pujaServicio.guardarPuja(puja)
        return "redirect:/liga/$idLiga/subastas"

    }

}

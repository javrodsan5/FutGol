package org.springframework.samples.futgol.puja

import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.jugador.JugadorServicio
import org.springframework.samples.futgol.liga.LigaServicio
import org.springframework.samples.futgol.subasta.SubastaServicio
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
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
                idLiga,
                principal
            ) && subastaServicio.existeSubastaPorLigaId(idLiga) == true
        ) {
            var idMiEquipo = equipoServicio.buscaMiEquipoEnLiga(idLiga, principal).id!!
            var subasta = subastaServicio.buscarSubastaPorLigaId(idLiga)
            val jugador = jugadorServicio.buscaJugadorPorId(idJugador)
            if (!jugadorServicio.existeJugadorEnEquipo(idJugador, idMiEquipo) && jugador in subasta!!.jugadores
            ) {
                model["puja"] = Puja()
                model["liga"] = ligaServicio.buscarLigaPorId(idLiga)!!
                return VISTA_PUJA
            }
        } else {
            return "redirect:/misligas"
        }
        return "redirect:/misligas"

    }

    @PostMapping("/liga/{idLiga}/subastas/{idJugador}")
    fun pujarJugador(
        puja: Puja, result: BindingResult,
        @PathVariable idLiga: Int,
        @PathVariable idJugador: Int,model: Model,
        principal: Principal
    ): String {
         if (result.hasErrors()) {
            model["liga"] = ligaServicio.buscarLigaPorId(idLiga)!!
             model["puja"] = puja
            return VISTA_PUJA
        }
        var equipo = equipoServicio.buscaMiEquipoEnLiga(idLiga, principal)!!
        puja.equipo = equipo
        puja.subasta = subastaServicio.buscarSubastaPorLigaId(idLiga)
        puja.jugador = jugadorServicio.buscaJugadorPorId(idJugador)
        pujaServicio.guardarPuja(puja)
        return "redirect:/liga/$idLiga/subastas"

    }

}

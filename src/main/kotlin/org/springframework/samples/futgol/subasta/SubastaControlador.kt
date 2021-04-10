package org.springframework.samples.futgol.subasta

import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.jugador.JugadorServicio
import org.springframework.samples.futgol.liga.LigaServicio
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.security.Principal

@Controller
class SubastaControlador(
    val subastaServicio: SubastaServicio,
    val ligaServicio: LigaServicio,
    val equipoServicio: EquipoServicio,
    val jugadorServicio: JugadorServicio
) {

    private val VISTA_SUBASTA_LIGA = "liga/subastas"

    @GetMapping("/liga/{idLiga}/subastas")
    fun jugadoresLibresSubasta(@PathVariable idLiga: Int, model: Model, principal: Principal): String {
        return if (ligaServicio.comprobarSiExisteLiga2(idLiga) == true && ligaServicio.estoyEnLiga2(
                idLiga, principal
            )
        ) {
            val subasta = subastaServicio.buscarSubastaPorLigaId(idLiga)!!
            model["jugadoresSinEquipo"] = subasta.jugadores!!
            model["liga"] = ligaServicio!!.buscarLigaPorId(idLiga)!!

            VISTA_SUBASTA_LIGA
        } else {
            "redirect:/misligas"
        }
    }

    @GetMapping("/liga/{idLiga}/vender/{idJugador}")
    fun sacarJugadorSubasta(
        @PathVariable idLiga: Int,
        @PathVariable idJugador: Int,
        principal: Principal, model: Model
    ): String {
        if (ligaServicio.comprobarSiExisteLiga2(idLiga) == true && ligaServicio.estoyEnLiga2(
                idLiga, principal
            ) && subastaServicio.existeSubastaPorLigaId(idLiga) == true && equipoServicio.tengoEquipo(idLiga, principal)
        ) {
            var idMiEquipo = equipoServicio.buscaMiEquipoEnLiga(idLiga, principal).id!!
            if (jugadorServicio.existeJugadorEnEquipo(idJugador, idMiEquipo)) {

                var subasta = subastaServicio.buscarSubastaPorLigaId(idLiga)!!
                subastaServicio.sacarJugadorSubasta(idJugador, subasta)
                model["liga"] = ligaServicio!!.buscarLigaPorId(idLiga)!!
                return "redirect:/liga/$idLiga/subastas"
            }
        } else {
            return "redirect:/misligas"
        }
        return "redirect:/misligas"
    }

}

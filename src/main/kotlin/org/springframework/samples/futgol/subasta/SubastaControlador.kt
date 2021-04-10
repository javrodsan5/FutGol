package org.springframework.samples.futgol.subasta

import org.springframework.samples.futgol.liga.LigaServicio
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.security.Principal

@Controller
class SubastaControlador(
    val subastaServicio: SubastaServicio, val ligaServicio: LigaServicio
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

}

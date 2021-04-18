package org.springframework.samples.futgol.movimientos

import org.springframework.samples.futgol.liga.LigaServicio
import org.springframework.samples.futgol.usuario.UsuarioServicio
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.security.Principal

@Controller
class MovimientoControlador(
    val movimientoServicio: MovimientoServicio, val usuarioServicio: UsuarioServicio, val ligaServicio: LigaServicio
) {
    private val VISTA_MOVIMIENTOS = "movimiento/movimientos"

    @GetMapping("/liga/{idLiga}/movimientos")
    fun movimientosLiga(model: Model, @PathVariable("idLiga") idLiga: Int, principal: Principal): String {
        if (ligaServicio.comprobarSiExisteLiga2(idLiga) == true && ligaServicio.estoyEnLiga2(idLiga, principal)) {
            model["liga"] = ligaServicio.buscarLigaPorId(idLiga)!!
            val movimientos = movimientoServicio.buscarMovimientosDeLigaPorId(idLiga)
            if (movimientos != null) {
                model["movimientos"] = movimientos
            }
            model["sonDeLiga"] = true

            return VISTA_MOVIMIENTOS

        }
        return "redirect:/misligas"
    }

    @GetMapping("/liga/{idLiga}/misMovimientos")
    fun movimientosUsuario(model: Model, @PathVariable("idLiga") idLiga: Int, principal: Principal): String {
        if (ligaServicio.comprobarSiExisteLiga2(idLiga) == true && ligaServicio.estoyEnLiga2(idLiga, principal)) {
            model["liga"] = ligaServicio.buscarLigaPorId(idLiga)!!

            val movimientos = this.movimientoServicio.buscarMovimientosPorUsuarioYLiga(principal.name, idLiga)
            if (movimientos != null) {
                model["misMovimientos"] = movimientos
            }
            model["sonDeUsuario"] = true
            return VISTA_MOVIMIENTOS
        }
        return "redirect:/misligas"
    }

}

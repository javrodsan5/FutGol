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


    fun auxMovimientos(model: Model, movimientos: Collection<Movimiento>, idLiga: Int) {
        if (movimientos != null) {
            model["liga"] = ligaServicio.buscarLigaPorId(idLiga)!!
            model["movimientos"] = movimientos
            model["vendedores"] = movimientoServicio.buscaVendedores(movimientos)
        }
    }

    @GetMapping("/liga/{idLiga}/movimientos")
    fun movimientosLiga(model: Model, @PathVariable("idLiga") idLiga: Int, principal: Principal): String {
        if (ligaServicio.comprobarSiExisteLiga2(idLiga) == true && ligaServicio.estoyEnLiga2(idLiga, principal)) {
            val movimientos = movimientoServicio.buscarMovimientosDeLigaPorId(idLiga)
            if (movimientos != null) {
                auxMovimientos(model, movimientos, idLiga)
            }
            model["sonDeLiga"] = true

            return VISTA_MOVIMIENTOS

        }
        return "redirect:/misligas"
    }

    @GetMapping("/liga/{idLiga}/misMovimientos")
    fun movimientosUsuario(model: Model, @PathVariable("idLiga") idLiga: Int, principal: Principal): String {
        if (ligaServicio.comprobarSiExisteLiga2(idLiga) == true && ligaServicio.estoyEnLiga2(idLiga, principal)) {
            val movimientos = usuarioServicio.usuarioLogueado(principal)?.user?.let {
                movimientoServicio.buscarMovimientosUsuario(it.username)
            }
            if (movimientos != null) {
                auxMovimientos(model, movimientos, idLiga)
            }
            model["sonDeUsuario"] = true
            return VISTA_MOVIMIENTOS
        }
        return "redirect:/misligas"
    }

}

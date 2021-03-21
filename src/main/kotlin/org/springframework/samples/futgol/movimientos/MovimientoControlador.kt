package org.springframework.samples.futgol.movimientos

import org.springframework.samples.futgol.liga.LigaServicio
import org.springframework.samples.futgol.usuario.Usuario
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
    private val VISTA_MOVIMIENTOS_LIGA = "movimiento/movimientosLiga"
    private val VISTA_MOVIMIENTOS_USUARIO = "movimiento/movimientosUsuario"


    fun buscaVendedores(movimientos: Collection<Movimiento>?): MutableList<Usuario> {
        val vendedores: MutableList<Usuario> = ArrayList()
        if (movimientos != null) {
            for (m in movimientos) {
                m.jugador?.name?.let { movimientoServicio.buscaVendedor(it) }?.let { vendedores.add(it) }
            }
        }
        return vendedores
    }

    @GetMapping("/liga/{idLiga}/movimientos")
    fun movimientosLiga(model: Model, @PathVariable("idLiga") idLiga: Int, principal: Principal): String {
        val usuario = usuarioServicio.usuarioLogueado(principal)
        val liga = ligaServicio.buscarLigaPorId(idLiga)
        var ligasUsuario = usuario?.user?.let { usuarioServicio.buscarLigasUsuario(it.username) }
        if (ligasUsuario != null) {
            for (l in ligasUsuario) {
                if (l.id == idLiga) {
                    val movimientos = movimientoServicio.buscarMovimientosDeLigaPorId(idLiga)
                    if (movimientos != null && liga != null) {
                        model["movimientos"] = movimientos
                        model["liga"] = liga
                        var vendedores = buscaVendedores(movimientos)
                        model["vendedores"] = vendedores
                        return VISTA_MOVIMIENTOS_LIGA
                    }
                }
            }
        }
        return "redirect:/misligas"
    }

    @GetMapping("/liga/{idLiga}/misMovimientos")
    fun movimientosUsuario(model: Model, @PathVariable("idLiga") idLiga: Int, principal: Principal): String {
        val usuario = usuarioServicio.usuarioLogueado(principal)
        val ligasUsuario = usuario?.user?.let { usuarioServicio.buscarLigasUsuario(it.username) }
        if (ligasUsuario != null) {
            for (l in ligasUsuario) {
                if (l.id == idLiga) {
                    var movimientos = usuario.user?.let { movimientoServicio.buscarMovimientosUsuario(it.username) }
                    if (movimientos != null) {
                        model["movimientos"] = movimientos
                        var vendedores = buscaVendedores(movimientos)
                        model["vendedores"] = vendedores
                        return VISTA_MOVIMIENTOS_USUARIO
                    }
                }
            }
        }
        return "redirect:/misligas"
    }

}

package org.springframework.samples.futgol.movimientos

import org.springframework.samples.futgol.usuario.UsuarioServicio
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.security.Principal

@Controller
class MovimientoControlador(
    val movimientoServicio: MovimientoServicio, val usuarioServicio: UsuarioServicio
) {
    private val VISTA_MOVIMIENTOS_LIGA = "movimiento/movimientosLiga"
    private val VISTA_MOVIMIENTOS_USUARIO = "movimiento/movimientosUsuario"


    fun buscaVendedores(movimientos: Collection<Movimiento>?): MutableList<org.springframework.samples.futgol.login.User> {
        var vendedores: MutableList<org.springframework.samples.futgol.login.User> = ArrayList()
        if (movimientos != null) {
            for (m in movimientos) {
                var vendedor = m.jugador?.name?.let { movimientoServicio.buscaVendedor(it) }
                if (vendedor != null) {
                    vendedores.add(vendedor)
                }
            }
        }
        return vendedores
    }

    @GetMapping("/liga/{idLiga}/movimientos")
    fun movimientosLiga(model: Model, @PathVariable("idLiga") idLiga: Int, principal: Principal): String {
        var usuario = usuarioServicio.usuarioLogueado(principal)
        var ligasUsuario = usuario?.user?.let { usuarioServicio.buscarLigasUsuario(it.username) }
        if (ligasUsuario != null) {
            for (l in ligasUsuario) {
                if (l.id == idLiga) {
                    var movimientos = movimientoServicio.buscarMovimientosDeLigaPorId(idLiga)
                    if (movimientos != null) {
                        model["movimientos"] = movimientos
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
        var usuario = usuarioServicio.usuarioLogueado(principal)
        var ligasUsuario = usuario?.user?.let { usuarioServicio.buscarLigasUsuario(it.username) }
        if (ligasUsuario != null) {
            for (l in ligasUsuario) {
                if (l.id == idLiga) {
                    var movimientos = usuario?.user?.let { movimientoServicio.buscarMovimientosUsuario(it.username) }
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

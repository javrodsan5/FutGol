package org.springframework.samples.futgol.intercambio

import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.jugador.JugadorServicio
import org.springframework.samples.futgol.liga.LigaServicio
import org.springframework.samples.futgol.usuario.UsuarioServicio
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.security.Principal

@Controller
class IntercambioControlador(
    val intercambioServicio: IntercambioServicio,
    val ligaServicio: LigaServicio,
    val equipoServicio: EquipoServicio,
    val jugadorServicio: JugadorServicio,
    val usuarioServicio: UsuarioServicio
) {

    private val VISTA_INTERCAMBIOS = "equipos/intercambios"


    @GetMapping("liga/{idLiga}/equipos/{idEquipo}/intercambios")
    fun intercambiosEquipo(
        model: Model, @PathVariable idLiga: Int, principal: Principal,
        @PathVariable("idEquipo") idEquipo: Int
    ): String {
        if (ligaServicio.comprobarSiExisteLiga2(idLiga) == true && ligaServicio.estoyEnLiga2(
                idLiga, principal) && equipoServicio.tengoEquipo(idLiga, principal)) {
                    val usuario = usuarioServicio.usuarioLogueado(principal)!!
            var idUsuario = usuario.id!!
            model["liga"] = ligaServicio.buscarLigaPorId(idLiga)!!
            if(intercambioServicio.existenIntercambiosUsuario(idUsuario)  == true) {
                model["intercambios"] = intercambioServicio.buscarIntercambiosUsuario(idUsuario)!!
            }
        }
        return VISTA_INTERCAMBIOS
    }
}

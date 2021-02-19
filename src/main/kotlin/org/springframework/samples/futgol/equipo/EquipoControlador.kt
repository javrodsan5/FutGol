package org.springframework.samples.futgol.equipo

import org.jsoup.Jsoup
import org.springframework.samples.futgol.liga.LigaServicio
import org.springframework.samples.futgol.usuario.Usuario
import org.springframework.samples.futgol.usuario.UsuarioServicio
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import java.security.Principal
import javax.validation.Valid

@Controller
class EquipoControlador(
    val ligaServicio: LigaServicio,
    val equipoServicio: EquipoServicio,
    val usuarioServicio: UsuarioServicio
) {

    private val VISTA_CREAEQUIPOS = "equipos/crearEditarEquipoUsuario"
    private val VISTA_WSEQUIPOS = "equipos/WScreaEquipos"
    private val VISTA_DETALLES_MIEQUIPO = "equipos/detallesMiEquipo"
    private val VISTA_DETALLES_OTROS_EQUIPOS = "equipos/detallesOtroEquipo"


    @GetMapping("/WSEquipos")
    fun iniciaWSEquipos(model: Model): String {
        return VISTA_WSEQUIPOS
    }

    @PostMapping("/WSEquipos")
    fun creaWSEquipos(model: Model): String {
        val doc = Jsoup.connect("https://fbref.com/es/comps/12/Estadisticas-de-La-Liga").get()
        val nombres = doc.select("#results107311_overall:first-of-type tbody tr")
        for (n in 0 until nombres.size) {
            var equipo = Equipo()
            var nombre = nombres[n].select("td:first-of-type a").text()
            equipo.name = nombre
            equipoServicio.saveEquipo(equipo)
        }
        return VISTA_WSEQUIPOS
    }

    @GetMapping("/liga/{idLiga}/nuevoEquipo")
    fun iniciarEquipo(model: Model, principal: Principal, @PathVariable idLiga: Int): String {
        return if (equipoServicio.tengoEquipo(idLiga, principal)) {
            var miEquipo = equipoServicio.buscaMiEquipoEnLiga(idLiga, principal)
            model["tengoEquipo"] = true
            model["equipo"] = miEquipo
            "redirect:/liga/$idLiga/miEquipo"
        } else {
            val equipo = Equipo()
            model["equipo"] = equipo
            VISTA_CREAEQUIPOS
        }
    }

    @PostMapping("/liga/{idLiga}/nuevoEquipo")
    fun procesoCrearEquipo(
        @Valid equipo: Equipo, result: BindingResult, @PathVariable(("idLiga")) idLiga: Int, principal: Principal,
        model: Model
    ): String {
        var liga = this.ligaServicio.buscarLigaPorId(idLiga)
        if (liga != null) {
            if (liga.id?.let { equipoServicio.checkEquipoEnLigaExists(equipo.name, it) }!!) {
                result.addError(FieldError("equipo", "name", "Ya existe una equipo con ese nombre en esta liga"))
            }
        }
        return if (result.hasErrors()) {
            model["equipo"] = equipo
            VISTA_CREAEQUIPOS
        } else {
            val usuario: Usuario? = usuarioServicio.usuarioLogueado(principal)
            equipo.usuario = usuario?.user
            equipo.dineroRestante = 25000000
            equipo.liga = liga
            this.equipoServicio.saveEquipo(equipo)
            "redirect:/liga/$idLiga/miEquipo"
        }
    }

    @GetMapping("liga/{idLiga}/miEquipo")
    fun detallesMiEquipo(
        model: Model, @PathVariable("idLiga") idLiga: Int, principal: Principal
    ): String {
        if (!equipoServicio.tengoEquipo(idLiga, principal)) {
            model["liga"] = ligaServicio.buscarLigaPorId(idLiga)!!
            model["SinEquipo"] = true
        } else {
            var miEquipo = equipoServicio.buscaMiEquipoEnLiga(idLiga, principal)
            model["tengoEquipo"] = true
            model["equipo"] = miEquipo
            var jugadores = miEquipo.jugadores
            model["jugadores"] = jugadores
        }
        return VISTA_DETALLES_MIEQUIPO
    }

    @GetMapping("liga/{nombreLiga}/equipos/{idEquipo}")
    fun detallesEquipo(
        model: Model, @PathVariable("nombreLiga") nombreLiga: String,
        principal: Principal,
        @PathVariable("idEquipo") idEquipo: Int
    ): String {
        var liga = ligaServicio.findLigaByName(nombreLiga)!!
        model["liga"] = liga
        var equipo = equipoServicio.buscaEquiposPorId(idEquipo)!!
        model["equipo"] = equipo
        var jugadores = equipo.jugadores
        model["jugadores"] = jugadores
        return if (equipo.name != liga.id?.let { equipoServicio.buscaMiEquipoEnLiga(it, principal).name }) {
            VISTA_DETALLES_OTROS_EQUIPOS
        } else {
            "redirect:/liga/" + liga.id + "/miEquipo"
        }
    }

}

package org.springframework.samples.futgol.equipo

import org.jsoup.Jsoup
import org.springframework.samples.futgol.liga.LigaServicio
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@Controller
class EquipoControlador(val ligaServicio: LigaServicio, val equipoServicio: EquipoServicio) {

    private val VISTA_WSEQUIPOS = "equipos/wsEquipos"
    private val VISTA_CREAEQUIPOS = "equipos/creaEquipos"


    @GetMapping("/WSEquipos")
    fun iniciaWSEquipos(model: Model): String {

        return VISTA_CREAEQUIPOS
    }

    @PostMapping("/WSEquipos")
    fun creaWSEquipos(equipo: Equipo, result: BindingResult, model: Model): String {
        val doc = Jsoup.connect("https://fbref.com/es/comps/12/Estadisticas-de-La-Liga").get()
        val nombres = doc.select("#results107311_overall:first-of-type tbody tr")
        for (n in 0 until nombres.size) {
            var equipo = Equipo()
            var nombre = nombres[n].select("td:first-of-type a").text()
            equipo.name = nombre
            equipoServicio.saveEquipo(equipo)
        }
        return VISTA_CREAEQUIPOS
    }

}

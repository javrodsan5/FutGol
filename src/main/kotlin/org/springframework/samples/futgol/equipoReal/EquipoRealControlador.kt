package org.springframework.samples.futgol.equipoReal

import org.jsoup.Jsoup
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@Controller
class EquipoRealControlador(val equipoRealServicio: EquipoRealServicio) {

    private val VISTA_WSEQUIPOS = "equipos/WScreaEquipos"

    @GetMapping("/WSEquipos")
    fun iniciaWSEquipos(model: Model): String {
        return VISTA_WSEQUIPOS
    }

    @PostMapping("/WSEquipos")
    fun creaWSEquipos(model: Model): String {
        var urlBase = "https://fbref.com/"
        var doc = Jsoup.connect("$urlBase/es/comps/12/Estadisticas-de-La-Liga").get()
        var nombres = doc.select("#results107311_overall:first-of-type tbody tr")
        for (n in 0 until nombres.size) {
            var equipo = EquipoReal()
            equipo.posicion = nombres[n].select("th").text().toInt()
            equipo.partidosJugados = nombres[n].select("td[data-stat=games]").text().toInt()
            equipo.partidosGanados = nombres[n].select("td[data-stat=wins]").text().toInt()
            equipo.partidosEmpatados = nombres[n].select("td[data-stat=draws]").text().toInt()
            equipo.partidosPerdidos = nombres[n].select("td[data-stat=losses]").text().toInt()
            equipo.golesAFavor = nombres[n].select("td[data-stat=goals_for]").text().toInt()
            equipo.golesEnContra = nombres[n].select("td[data-stat=goals_against]").text().toInt()
            equipo.diferenciaGoles = nombres[n].select("td[data-stat=goal_diff]").text().toInt()
            equipo.puntos = nombres[n].select("td[data-stat=points]").text().toInt()
            equipo.name = nombres[n].select("td:first-of-type a").text()

            var linkEquipo = nombres[n].select("td:first-of-type a").attr("href")
            var doc2 = Jsoup.connect("$urlBase" + linkEquipo).get()
            equipo.escudo = doc2.select("div.media-item.logo img").attr("src")

            equipoRealServicio.guardarEquipo(equipo)
        }
        return VISTA_WSEQUIPOS
    }

}

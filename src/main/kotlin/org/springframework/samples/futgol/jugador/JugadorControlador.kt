package org.springframework.samples.futgol.jugador

import org.jsoup.Jsoup
import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import java.util.stream.Collectors

@Controller
class JugadorControlador(val equipoServicio: EquipoServicio, val jugadorServicio: JugadorServicio) {

    private val VISTA_WSJUGADORES = "jugadores/wsJugadores"


    @GetMapping("/WSJugadores")
    fun iniciaWSJugadores(model: Model): String {
        return VISTA_WSJUGADORES
    }

    @PostMapping("/WSJugadores")
    fun creaWSJugadores(model: Model): String {
        val urlBase = "https://www.transfermarkt.es"
        val doc = Jsoup.connect("$urlBase/laliga/startseite/wettbewerb/ES1/plus/?saison_id=2020").get()
        val linksEquipos =
            doc.select("div.box.tab-print td a.vereinprofil_tooltip").map { col -> col.attr("href") }.stream()
                .distinct().collect(Collectors.toList())
        for (linkEquipo in linksEquipos) {
            val doc2 = Jsoup.connect("$urlBase" + linkEquipo).get()
            val linkPlantilla =
                doc2.select("li#vista-general.first-button a").map { col -> col.attr("href") }.stream().distinct()
                    .collect(Collectors.toList())
            val doc3 = Jsoup.connect("$urlBase" + linkPlantilla[0]).get()
            val precioJugadores = doc3.select("div.responsive-table tbody tr td.rechts.hauptlink")
            val jugadores = doc3.select("div#yw1.grid-view table.items tbody tr:first-of-type")
            jugadores.removeAt(0)
            for (n in 0 until jugadores.size) {
                var jugador = Jugador()
                var persona = jugadores[n].select("td div.di.nowrap:first-of-type span a")
                var nombre = persona.attr("title")

                var linkDetallePersona = persona.attr("href")
                val doc4 = Jsoup.connect("$urlBase" + linkDetallePersona).get()
                var foto = doc4.select("div.dataBild img").attr("src")
                var estado = "En forma"
                if (!jugadores[n].select("td span.verletzt-table").isEmpty()) {
                    estado = "Lesionado"
                } else if (!jugadores[n].select("td span.ausfall-6-table").isEmpty()) {
                    estado = "Sancionado/No disponible"
                }
                var precio = precioJugadores[n].text()
                var precioD = 0.1
                if (!precio.isEmpty()) {
                    if (precio.contains("mill")) {
                        precioD = precio.substringBefore(" mil").replace(",", ".").toDouble()
                    } else {
                        precioD = precio.substringBefore(" mil").replace(",", ".").toDouble() / 1000//pasar a millones
                    }
                }
                jugador.name=nombre
                jugador.estadoLesion=estado
                jugador.valor=precioD
                jugador.foto=foto
                jugadorServicio.saveJugador(jugador)
            }
        }
        return VISTA_WSJUGADORES
    }

}

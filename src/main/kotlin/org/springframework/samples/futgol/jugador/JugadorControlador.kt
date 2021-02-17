package org.springframework.samples.futgol.jugador

import org.jsoup.Jsoup
import org.springframework.samples.futgol.equipo.Equipo
import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping

@Controller
class JugadorControlador(val equipoServicio: EquipoServicio, val jugadorServicio: JugadorServicio) {

    private val VISTA_WSJUGADORES = "jugadores/wsJugadores"


    @GetMapping("/getJugadores")
    fun wsEquipos(model: Model): String {
        val doc = Jsoup.connect("https://fbref.com/es/comps/12/Estadisticas-de-La-Liga").get()
        val nombres = doc.select("#results107311_overall:first-of-type tr td:first-of-type a").text()
        print(nombres)
        var numJugadores = 0
        model["numJugadores"] = numJugadores
        return VISTA_WSJUGADORES
    }
}

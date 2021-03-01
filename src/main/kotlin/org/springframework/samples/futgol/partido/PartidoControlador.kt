package org.springframework.samples.futgol.partido

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException

@Controller
class PartidoControlador(val partidoServicio: PartidoServicio) {

    private val VISTA_WSPARTIDOS = "partidos/wsPartidos"
    private val VISTA_PARTIDOS = "partidos/partidos"
    private val VISTA_FILTRAR_PARTIDOS = "partidos/filtroPartidos"


    @GetMapping("/WSPartidos")
    fun iniciaWSPartidos(model: Model): String {
        return VISTA_WSPARTIDOS
    }

    @PostMapping("/WSPartidos")
    fun creaWSPartidos(model: Model): String {


        this.partidoServicio.wsPartidos()
        return VISTA_WSPARTIDOS
    }
}

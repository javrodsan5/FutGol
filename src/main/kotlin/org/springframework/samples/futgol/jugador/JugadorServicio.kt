package org.springframework.samples.futgol.jugador

import org.apache.tomcat.util.http.parser.HttpParser.isNumeric
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.equipoReal.EquipoRealServicio
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.ArrayList
import java.util.stream.Collectors

@Service
class JugadorServicio {

    private var jugadorRepositorio: JugadorRepositorio? = null

    @Autowired
    private val equipoRealServicio: EquipoRealServicio? = null

    @Autowired
    fun JugadorServicio(jugadorRepositorio: JugadorRepositorio) {
        this.jugadorRepositorio = jugadorRepositorio
    }

    @Transactional()
    @Throws(DataAccessException::class)
    fun guardarJugador(jugador: Jugador) {
        jugadorRepositorio?.save(jugador)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscaJugadorPorId(idJugador: Int): Jugador? {
        return jugadorRepositorio?.findById(idJugador)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscaJugadorPorNombre(nombre: String): Jugador? {
        return jugadorRepositorio?.buscarJugadorPorNombre(nombre)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscaJugadorPorNombreYEquipo(nombreJugador: String, nombreEquipo: String): Jugador? {
        return jugadorRepositorio?.buscarJugadorPorNombreyEquipo(nombreJugador, nombreEquipo)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun existeJugador(nombreJugador: String, equipo: String): Boolean? {
        var res = false
        if(equipo!="" && equipoRealServicio?.existeEquipoReal(equipo) == true){
        var jugadores = equipoRealServicio?.buscarEquipoRealPorNombre(equipo)?.jugadores
        if (jugadores != null) {
            for (j in jugadores) {
                if (j.name == nombreJugador) {
                    res = true
                    break
                }
            }
        }
        }
        return res
    }

    fun webScrapingJugadoresTransfermarkt() {
        var urlBase = "https://www.transfermarkt.es"
        var doc = Jsoup.connect("$urlBase/laliga/startseite/wettbewerb/ES1/plus/?saison_id=2020").get()
        var linksEquipos =
            doc.select("div.box.tab-print td a.vereinprofil_tooltip").map { col -> col.attr("href") }.stream()
                .distinct().collect(Collectors.toList())
        for (linkEquipo in linksEquipos) {
            var doc2 = Jsoup.connect("$urlBase" + linkEquipo).get()
            var nombreEquipo =
                doc2.select("div.dataName h1 span").text().replace("Atlético de Madrid", "Atlético Madrid")
                    .replace("CF", "").replace("FC", "").replace("SD", "")
                    .replace("Real Betis Balompié", "Real Betis").replace("Deportivo", "")
                    .replace("Real Valladolid", "Valladolid")
                    .replace("CA", "").replace("UD", "").replace("RC Celta de Vigo", "Celta Vigo").trim()
            println(nombreEquipo)
            var linkPlantilla =
                doc2.select("li#vista-general.first-button a").map { col -> col.attr("href") }.stream().distinct()
                    .collect(Collectors.toList())
            var doc3 = Jsoup.connect("$urlBase" + linkPlantilla[0]).get()
            var precioJugadores = doc3.select("div.responsive-table tbody tr td.rechts.hauptlink")
            var jugadores = doc3.select("div#yw1.grid-view table.items tbody tr:first-of-type")
            jugadores.removeAt(0)
            for (n in 0 until jugadores.size) {
                var jugador = Jugador()
                var persona = jugadores[n].select("td div.di.nowrap:first-of-type span a")
                var nombre = persona.attr("title")

                var linkDetallePersona = persona.attr("href")
                var doc4 = Jsoup.connect("$urlBase" + linkDetallePersona).get()
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
                    precioD = if (precio.contains("mill")) {
                        precio.substringBefore(" mil").replace(",", ".").toDouble()
                    } else {
                        precio.substringBefore(" mil").replace(",", ".").toDouble() / 1000//pasar a millones
                    }
                }
                jugador.name = nombre
                jugador.estadoLesion = estado
                jugador.valor = precioD
                jugador.foto = foto

                var equipoReal = equipoRealServicio?.buscarEquipoRealPorNombre(nombreEquipo)
                jugador.club = equipoReal
                guardarJugador(jugador)
            }
        }

    }

    fun webScrapingJugadoresFbref() {
        var urlBase = "https://fbref.com"
        var doc = Jsoup.connect("$urlBase/es/comps/12/Estadisticas-de-La-Liga").get()
        var linksEquipos = doc.select("#results107311_overall:first-of-type tr td:first-of-type a")
            .map { col -> col.attr("href") }.stream()
            .collect(Collectors.toList()) //todos los links de los equipos de la liga
        var linksJug: MutableList<String> = ArrayList()
        for (linkEquipo in linksEquipos) {

            val doc2 = Jsoup.connect("$urlBase" + linkEquipo).get()

            linksJug.addAll(
                doc2.select("table.min_width.sortable.stats_table#stats_standard_10731 th a:first-of-type")
                    .map { col -> col.attr("href") }.stream().distinct().collect(Collectors.toList())
            )
            linksJug =
                linksJug.stream().distinct().collect(Collectors.toList()) //todos los links jugadores de la liga

        }

        var l: List<String?> = ArrayList()
        try {
            l = Files.lines(Paths.get("CambioNombresJugadores.txt")).collect(Collectors.toList())
        } catch (e: IOException) {
            println("No se puede leer el fichero de nombres.")
        }

        for (linkJugador in linksJug) {
            var doc3 = Jsoup.connect("$urlBase" + linkJugador).get()
            var nombreJugador = doc3.select("h1[itemprop=name]").text().trim()
            var equipo = doc3.select("div#meta p").last().text().replace("Club : ", "").trim()
            if (equipoRealServicio?.existeEquipoReal(equipo) == true){
                for (n in 0 until l.size) {
                    var linea = l[n]?.split(",")
                    if (linea?.size!! >= 3) {
                        if (linea?.get(2).equals(equipo) && linea?.get(0).equals(nombreJugador)) {
                            nombreJugador = linea?.get(1).toString()
                        }
                    } else {
                        if (linea?.get(0).equals(nombreJugador)) {
                            nombreJugador = linea?.get(1).toString()
                        }
                    }
                }
            if (this.existeJugador(nombreJugador, equipo) == true) {
                println(nombreJugador)
                var j = this.buscaJugadorPorNombreYEquipo(nombreJugador, equipo)
                var element: Elements? = doc3.select("div.players#info div#meta p")
                if (j != null) {
                    for (n in 0 until element?.size!!) {
                        if (element[n].text().contains("Posición:")) {
                            j.posicion = element[n].text().split("▪")[0].substringAfter(": ").trim().substring(0,2)
                            println(j.posicion)
                        }
                        if (element[n].text().contains("Pie primario:")) {
                            if (element[n].text().contains("%")) {
                                j.piePrimario =
                                    element[n].text().split("▪")[1].substringAfter("% ").replace("*", "") //pie primario

                            } else {
                                j.piePrimario = element[n].text().split("▪")[1].substringAfter(": ") //pie primario
                            }
                            println(j.piePrimario)
                        }
                        if (element[n].text().contains("cm") && isNumeric(element[n].text()[0].toInt())) {
                            j.altura = element[n].text().split(",")[0].substringBefore("cm").trim().toDouble()
                            println(j.altura)
                        }
                        if (element[n].text().contains("kg") && isNumeric(element[n].text()[0].toInt())) {
                            j.peso = element[n].text().split(",")[1].substringBefore("kg").trim().toDouble()
                            println(j.peso)
                        }
                        if (element[n].text().contains("Nacimiento:")) {
                            j.lugarFechaNacimiento = element[n].text().substringAfter("Nacimiento: ")
                            println(j.lugarFechaNacimiento)
                        }
                    }
                    this.guardarJugador(j)
                }
            }
        }
    }
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscaTodosJugadores(): Collection<Jugador>? {
        return jugadorRepositorio?.findAll()
    }
}

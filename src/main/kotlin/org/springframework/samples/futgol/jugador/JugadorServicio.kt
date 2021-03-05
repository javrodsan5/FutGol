package org.springframework.samples.futgol.jugador

import org.apache.tomcat.util.http.parser.HttpParser.isNumeric
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.equipoReal.EquipoRealServicio
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.HashSet

@Service
class JugadorServicio {

    private var jugadorRepositorio: JugadorRepositorio? = null

    @Autowired
    private val equipoRealServicio: EquipoRealServicio? = null

    @Autowired
    private val equipoServicio: EquipoServicio? = null

    @Autowired
    private val jugadorServicio: JugadorServicio? = null

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
    fun existeJugadorId(idJugador: Int): Boolean? {
        return jugadorRepositorio?.findAll()?.stream()?.anyMatch { x -> x.id == idJugador }
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun existeJugadorNombre(nombreJugador: String): Boolean? {
        return jugadorRepositorio?.findAll()?.stream()?.anyMatch { x -> x.name == nombreJugador }
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscaJugadorPorNombre(nombreJugador: String): Jugador? {
        return jugadorRepositorio?.findByName(nombreJugador)
    }

    fun jugadoresAsignablesLiga(idLiga: Int): Collection<Jugador>? {
        var jugadores = this.buscaTodosJugadores()
        var listaPosiblesJugador = ArrayList<Jugador>()
        if (jugadores != null) {
            for (jugador in jugadores) {
                if (jugador.equipos.stream().noneMatch { x -> x.liga?.id == idLiga }) {
                    listaPosiblesJugador.add(jugador)
                }
            }
        }
        return listaPosiblesJugador
    }

    fun asignarjugadoresNuevoEquipo(idLiga: Int): MutableSet<Jugador> {
        var jugadoresAsignables = this.jugadoresAsignablesLiga(idLiga)
        var porterosA = ArrayList<Jugador>()
        var defensasA = ArrayList<Jugador>()
        var centrocamA = ArrayList<Jugador>()
        var delanterosA = ArrayList<Jugador>()
        var misJugadores = HashSet<Jugador>()

        if (jugadoresAsignables != null) {
            for (jugador in jugadoresAsignables) {
                if (jugador.posicion == "PO") {
                    porterosA.add(jugador)
                } else if (jugador.posicion == "DF") {
                    defensasA.add(jugador)
                } else if (jugador.posicion == "CC") {
                    centrocamA.add(jugador)
                } else {
                    delanterosA.add(jugador)
                }
            }
        }
        var porteros = porterosA.shuffled().subList(0, 2)
        var defensas = defensasA.shuffled().subList(0, 6)
        var centrocam = centrocamA.shuffled().subList(0, 6)
        var delanteros = delanterosA.shuffled().subList(0, 4)
        misJugadores.addAll(porteros)
        misJugadores.addAll(defensas)
        misJugadores.addAll(centrocam)
        misJugadores.addAll(delanteros)
        return misJugadores
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscaJugadorPorNombreYEquipo(nombreJugador: String, nombreEquipo: String): Jugador? {
        return jugadorRepositorio?.buscarJugadorPorNombreyEquipo(nombreJugador, nombreEquipo)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun existeJugadorEquipo(nombreJugador: String, equipo: String): Boolean? {
        var res = false
        if (equipo != "" && equipoRealServicio?.existeEquipoReal(equipo) == true) {
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

    fun checkJugadorEnEquipo(idJugador: Int, idEquipo: Int): Boolean {
        var estaEnEquipo = false
        var equipo = equipoServicio?.buscaEquiposPorId(idEquipo)
        var jugador = jugadorServicio?.buscaJugadorPorId(idJugador)!!
        for (j in equipo!!.jugadores) {
            if (j.name == jugador.name) {
                estaEnEquipo = true
                break
            }
        }
        return estaEnEquipo
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
            var linkPlantilla =
                doc2.select("li#vista-general.first-button a").map { col -> col.attr("href") }.stream().distinct()
                    .collect(Collectors.toList())
            var doc3 = Jsoup.connect("$urlBase" + linkPlantilla[0]).get()
            var precioJugadores = doc3.select("div.responsive-table tbody tr td.rechts.hauptlink")
            var jugadores = doc3.select("div#yw1.grid-view table.items tbody tr:first-of-type")
            jugadores.removeAt(0)
            for (n in 0 until jugadores.size) {
                var j = Jugador()
                var persona = jugadores[n].select("td div.di.nowrap:first-of-type span a")
                var nombre = persona.attr("title")

                    var linkDetallePersona = persona.attr("href")
                    var doc4 = Jsoup.connect("$urlBase" + linkDetallePersona).get()
                    var estado = "En forma"
                    if (!jugadores[n].select("td span.verletzt-table").isEmpty()) {
                        estado = "Lesionado"
                    } else if (!jugadores[n].select("td span.ausfall-6-table").isEmpty()) {
                        estado = "Sancionado/No disponible"
                    }

                    var precio = precioJugadores[n].text()
                    var precioD = 0.1

                    var foto = doc4.select("div.dataBild img").attr("src")

                    if (!precio.isEmpty()) {
                        precioD = if (precio.contains("mill")) {
                            precio.substringBefore(" mil").replace(",", ".").toDouble()
                        } else {
                            precio.substringBefore(" mil").replace(",", ".").toDouble() / 1000//pasar a millones
                        }
                    }
                    var equipoReal = equipoRealServicio?.buscarEquipoRealPorNombre(nombreEquipo)

                if (this.existeJugadorEquipo(nombre, nombreEquipo) == true) {
                    println("Existe " + nombre)
                    j = this.buscaJugadorPorNombreYEquipo(nombre, nombreEquipo)!!
                    if(estado!=j.estadoLesion || precioD!=j.valor || j.club?.id!=equipoReal?.id){
                        println("Cambiamos algún atributo")
                        j.estadoLesion=estado
                        j.valor= precioD
                        j.club= equipoReal
                        guardarJugador(j)
                    }
                }else{
                    println("No existe " + nombre)
                    j.name = nombre
                    j.estadoLesion = estado
                    j.valor = precioD
                    j.foto = foto
                    j.club= equipoReal
                    guardarJugador(j)
                }

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
            if (this.existeJugadorEquipo(nombreJugador, equipo) == true) {
                var j = this.buscaJugadorPorNombreYEquipo(nombreJugador, equipo)
                if(j?.posicion=="") {
                    println(nombreJugador)
                    var element: Elements? = doc3.select("div.players#info div#meta p")
                    if (j != null) {
                        for (n in 0 until element?.size!!) {
                            if (element[n].text().contains("Posición:")) {
                                j.posicion = element[n].text().split("▪")[0].substringAfter(": ").trim().substring(0, 2)
                            }

                            if (element[n].text().contains("Pie primario:")) {
                                if (element[n].text().contains("%")) {
                                    j.piePrimario =
                                        element[n].text().split("▪")[1].substringAfter("% ")
                                            .replace("*", "") //pie primario

                                } else {
                                    j.piePrimario = element[n].text().split("▪")[1].substringAfter(": ") //pie primario
                                }
                            }
                            if (j.piePrimario == "") {
                                j.piePrimario = "Derecha"
                            }

                            if (element[n].text().contains("cm") && isNumeric(element[n].text()[0].toInt())) {
                                j.altura = element[n].text().split(",")[0].substringBefore("cm").trim().toDouble()
                            }

                            if (element[n].text().contains("kg") && isNumeric(element[n].text()[0].toInt())) {
                                j.peso = element[n].text().split(",")[1].substringBefore("kg").trim().toDouble()
                            }
                            if (element[n].text().contains("Nacimiento:")) {
                                var lugarFechaNacimiento = element[n].text().substringAfter("Nacimiento: ")
                                var x = lugarFechaNacimiento.length - 3
                                j.lugarFechaNacimiento = lugarFechaNacimiento.substring(0, x)
                            }
                        }
                        this.guardarJugador(j)
                    }
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

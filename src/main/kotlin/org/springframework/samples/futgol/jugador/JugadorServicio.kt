package org.springframework.samples.futgol.jugador

import org.apache.tomcat.util.http.parser.HttpParser.isNumeric
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.equipoReal.EquipoRealServicio
import org.springframework.samples.futgol.estadisticaJugador.EstadisticaJugadorServicio
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
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
    private val estadisticaJugadorServicio: EstadisticaJugadorServicio? = null

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
        return jugadorRepositorio?.existeJugadorId(idJugador)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun tienenMismaPos(idJugador1: Int, idJugador2: Int): Boolean? {
        return buscaJugadorPorId(idJugador1)?.posicion == buscaJugadorPorId(idJugador2)?.posicion

    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun existeJugadorNombre(nombreJugador: String): Boolean? {
        return jugadorRepositorio?.existeJugadorNombre(nombreJugador)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscaTodosJugadores(): Collection<Jugador>? {
        return jugadorRepositorio?.findAll()
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscarJugadoresSinPosicion(): Collection<Jugador>? {
        return jugadorRepositorio?.buscarJugadoresSinPosicion()
    }

    @Transactional()
    @Throws(DataAccessException::class)
    fun eliminarJugador(idJugador: Int) {
        jugadorRepositorio?.deleteJugadorById(idJugador)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscaJugadorPorNombre(nombreJugador: String): Jugador? {
        return jugadorRepositorio?.findByName(nombreJugador)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun tieneEstadisticas(idJugador: Int): Boolean? {
        return estadisticaJugadorServicio?.tieneAlgunaEstadisticaJugador(idJugador)
    }

    @Transactional(readOnly = true)
    fun mediaEstadisticasJugador(idJugador: Int): List<Double>? {
        var medias: MutableList<Double> = ArrayList()
        if (existeJugadorId(idJugador) == true && tieneEstadisticas(idJugador) == true) {
            var jugador = buscaJugadorPorId(idJugador)
            var estadisticasJugador = jugador!!.estadisticas
            var puntos = 0.0
            var minutos = 0.0
            var tAmarillas = 0.0
            var tRojas = 0.0
            var numeroEstadisticas = estadisticasJugador.size
            if (jugador.posicion == "PO") {
                var salvadas = 0.0
                var disparosRecibidos = 0.0
                var golesRecibidos = 0.0
                for (e in estadisticasJugador) {
                    salvadas += e.salvadas
                    disparosRecibidos += e.disparosRecibidos
                    golesRecibidos += e.golesRecibidos
                    puntos += e.puntos
                    minutos += e.minutosJugados
                    tAmarillas += e.tarjetasAmarillas
                    tRojas += e.tarjetasRojas
                }
                medias.add(Math.round(salvadas / numeroEstadisticas * 100) / 100.0)
                medias.add(Math.round(disparosRecibidos / numeroEstadisticas * 100) / 100.0)
                medias.add(Math.round(golesRecibidos / numeroEstadisticas * 100) / 100.0)
                medias.add(Math.round(puntos / numeroEstadisticas * 100) / 100.0)
                medias.add(Math.round(minutos / numeroEstadisticas * 100) / 100.0)
                medias.add(Math.round(tAmarillas / numeroEstadisticas * 100) / 100.0)
                medias.add(Math.round(tRojas / numeroEstadisticas * 100) / 100.0)
            } else if (jugador.posicion == "DF") {
                var robos = 0.0
                var bloqueos = 0.0
                var asistencias = 0.0
                for (e in estadisticasJugador) {
                    robos += e.robos
                    bloqueos += e.bloqueos
                    asistencias += e.asistencias
                    puntos += e.puntos
                    minutos += e.minutosJugados
                    tAmarillas += e.tarjetasAmarillas
                    tRojas += e.tarjetasRojas
                }
                medias.add(Math.round(robos / numeroEstadisticas * 100) / 100.0)
                medias.add(Math.round(bloqueos / numeroEstadisticas * 100) / 100.0)
                medias.add(Math.round(asistencias / numeroEstadisticas * 100) / 100.0)
                medias.add(Math.round(puntos / numeroEstadisticas * 100) / 100.0)
                medias.add(Math.round(minutos / numeroEstadisticas * 100) / 100.0)
                medias.add(Math.round(tAmarillas / numeroEstadisticas * 100) / 100.0)
                medias.add(Math.round(tRojas / numeroEstadisticas * 100) / 100.0)
            } else {
                var goles = 0.0
                var dispPuerta = 0.0
                var dispTotales = 0.0
                var asistencias = 0.0
                var penaltisMarcados = 0.0
                var penaltisLanzados = 0.0
                for (e in estadisticasJugador) {
                    goles += e.goles
                    dispPuerta += e.disparosPuerta
                    dispTotales += e.disparosTotales
                    penaltisLanzados += e.penaltisLanzados
                    penaltisMarcados += e.penaltisMarcados
                    asistencias += e.asistencias
                    puntos += e.puntos
                    minutos += e.minutosJugados
                    tAmarillas += e.tarjetasAmarillas
                    tRojas += e.tarjetasRojas
                }
                medias.add(Math.round(goles / numeroEstadisticas * 100) / 100.0)
                medias.add(Math.round(asistencias / numeroEstadisticas * 100) / 100.0)
                medias.add(Math.round(dispPuerta / numeroEstadisticas * 100) / 100.0)
                medias.add(Math.round(dispTotales / numeroEstadisticas * 100) / 100.0)
                medias.add(Math.round(penaltisLanzados / numeroEstadisticas * 100) / 100.0)
                medias.add(Math.round(penaltisMarcados / numeroEstadisticas * 100) / 100.0)
                medias.add(Math.round(puntos / numeroEstadisticas * 100) / 100.0)
                medias.add(Math.round(minutos / numeroEstadisticas * 100) / 100.0)
                medias.add(Math.round(tAmarillas / numeroEstadisticas * 100) / 100.0)
                medias.add(Math.round(tRojas / numeroEstadisticas * 100) / 100.0)
            }
        }
        return medias

    }

    fun jugadoresAsignablesLiga(idLiga: Int): Collection<Jugador>? {
        return this.buscaTodosJugadores()?.filter { x -> x.equipos.none { y -> y.liga?.id == idLiga } }
    }

    fun asignarjugadoresNuevoEquipo(idLiga: Int): MutableSet<Jugador> {
        var jugadoresAsignables = this.jugadoresAsignablesLiga(idLiga)
        var misJugadores = HashSet<Jugador>()

        misJugadores.addAll(jugadoresAsignables?.filter { x -> x.posicion == "PO" }?.shuffled()?.subList(0, 2)!!)
        misJugadores.addAll(jugadoresAsignables.filter { x -> x.posicion == "DF" }.shuffled().subList(0, 6))
        misJugadores.addAll(jugadoresAsignables.filter { x -> x.posicion == "CC" }.shuffled().subList(0, 6))
        misJugadores.addAll(jugadoresAsignables.filter { x -> x.posicion == "DL" }.shuffled().subList(0, 4))
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
        return jugadorRepositorio?.existeJugadorEquipo(nombreJugador, equipo)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscaJugadoresOrdenPuntos(): Collection<Jugador>? {
        return jugadorRepositorio?.buscarJugadoresOrdenPuntos()
    }

    fun existeJugadorEnEquipo(idJugador: Int, idEquipo: Int): Boolean {
        var equipo = equipoServicio?.buscaEquiposPorId(idEquipo)
        return equipo?.jugadores?.any { x -> x.id == idJugador }!!
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
                var doc4 = Jsoup.connect(urlBase + linkDetallePersona).get()
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
                    if (estado != j.estadoLesion || precioD != j.valor || j.club?.id != equipoReal?.id) {
                        println("Cambiamos algún atributo")
                        j.estadoLesion = estado
                        j.valor = precioD
                        j.club = equipoReal
                        guardarJugador(j)
                    }
                } else {

                    println("No existe " + nombre)
                    j.name = nombre
                    j.estadoLesion = estado
                    j.valor = precioD
                    j.foto = foto
                    j.club = equipoReal
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

            val doc2 = Jsoup.connect(urlBase + linkEquipo).get()

            linksJug.addAll(
                doc2.select("table.min_width.sortable.stats_table#stats_standard_10731 th a:first-of-type")
                    .map { col -> col.attr("href") }.stream().distinct().collect(Collectors.toList())
            )
            linksJug =
                linksJug.stream().distinct().collect(Collectors.toList()) //todos los links jugadores de la liga

        }

        var l: List<String?> = ArrayList()
        try {
            l = Files.lines(Paths.get("src/main/resources/wsFiles/CambioNombresJugadores.txt"))
                .collect(Collectors.toList())
        } catch (e: IOException) {
            println("No se puede leer el fichero de nombres.")
        }

        for (linkJugador in linksJug) {
            var doc3 = Jsoup.connect(urlBase + linkJugador).get()
            var nombreJugador = doc3.select("h1[itemprop=name]").text().trim()
            var equipo = doc3.select("div#meta p").last().text().replace("Club : ", "").trim()
            if (equipoRealServicio?.existeEquipoReal(equipo) == true) {
                for (element in l) {
                    var linea = element?.split(",")
                    if (linea?.size!! >= 3) {
                        if (linea[2] == equipo && linea[0] == nombreJugador) {
                            nombreJugador = linea[1]
                        }
                    } else {
                        if (linea[0] == nombreJugador) {
                            nombreJugador = linea[1]
                        }
                    }
                }
                if (this.existeJugadorEquipo(nombreJugador, equipo) == true) {
                    var j = this.buscaJugadorPorNombreYEquipo(nombreJugador, equipo)
                    if (j?.posicion == "") {
                        println(nombreJugador)
                        var element: Elements? = doc3.select("div.players#info div#meta p")
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

package org.springframework.samples.futgol.estadisticaJugador

import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.jugador.JugadorServicio
import org.springframework.samples.futgol.partido.PartidoServicio
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.text.Normalizer
import java.util.*
import java.util.stream.Collectors

@Service
class EstadisticaJugadorServicio {

    private var estadisticaJugadorRepositorio: EstadisticaJugadorRepositorio? = null
    private val QUITAACENTOS = "\\p{InCombiningDiacriticalMarks}+".toRegex()

    @Autowired
    private val partidoServicio: PartidoServicio? = null

    @Autowired
    private val jugadorServicio: JugadorServicio? = null

    @Autowired
    fun EstadisticaJugadorServicio(estadisticaJugadorRepositorio: EstadisticaJugadorRepositorio) {
        this.estadisticaJugadorRepositorio = estadisticaJugadorRepositorio
    }

    fun quitaTildes(nombre: String): String {
        val temp = Normalizer.normalize(nombre, Normalizer.Form.NFD)
        return QUITAACENTOS.replace(temp, "")
    }

    @Transactional()
    @Throws(DataAccessException::class)
    fun guardarEstadistica(estadisticaJugador: EstadisticaJugador) {
        estadisticaJugadorRepositorio?.save(estadisticaJugador)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscarEstadisticaPorJugadorPartido(jugador: String, equipo: String, idPartido: Int): EstadisticaJugador? {
        return estadisticaJugadorRepositorio?.buscarEstadisticaPorJugadorPartido(jugador, equipo, idPartido)
    }


    fun equiposPartidosEstadisticasJugadores() {
        var urlBase = "https://fbref.com"
        var doc = Jsoup.connect("$urlBase/es/comps/12/Estadisticas-de-La-Liga").get()
        var linksEquipos = doc.select("#results107311_overall:first-of-type tr td:first-of-type a")
            .map { col -> col.attr("href") }.stream()
            .collect(Collectors.toList()) //todos los links de los equipos de la liga
        var linksJug: MutableList<String> = ArrayList()
        for (n in 0 until linksEquipos.size) {
            val doc2 = Jsoup.connect("$urlBase" + linksEquipos[n]).get()

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

        for (n in 0 until linksJug.size) {
            var doc3 = Jsoup.connect("$urlBase" + linksJug[n]).get()
            var nombreJugador = doc3.select("h1[itemprop=name]").text().trim()

            var textoEquipo = doc3.select("div#meta p").last().text()
            var equipo= ""
            if(textoEquipo.contains("Club")) {
                equipo= textoEquipo.replace("Club : ", "").trim()
            }

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
                if (jugadorServicio?.existeJugador(nombreJugador, equipo) == true) {
                    println(nombreJugador)
                    if (!doc3.select("table#stats_standard_dom_lg.min_width.sortable.stats_table.shade_zero tbody tr")
                            .isEmpty()
                    ) {
                        var ultimaTemporada =
                            doc3.select("table#stats_standard_dom_lg.min_width.sortable.stats_table.shade_zero tbody tr")
                                .last()
                        var nombreUltimaTemporada =
                            (ultimaTemporada.select("th").first().text()) //tiene que ser 2020-2021
                        var ligaUltimaTemporada = ultimaTemporada.select("td")[3].text()
                        var partidosUltimaTemporada = ultimaTemporada.select("td").last().select("a").attr("href")
                        if (nombreUltimaTemporada.equals("2020-2021") && ligaUltimaTemporada.contains("La Liga")) {
                            var doc4 = Jsoup.connect("$urlBase" + partidosUltimaTemporada).get()
                            var doc5 = doc4
                            var filtro2 = ""

                            if (doc4.select("div.filter").size >= 2) {
                                var filtroCompeticiones = doc4.select("div.filter").first().select("a")
                                var filtro = ""
                                for (n in 0 until filtroCompeticiones.size) {
                                    if (filtroCompeticiones[n].text() == "2020-2021 La Liga") {
                                        filtro = filtroCompeticiones[n].attr("href")
                                    }
                                }
                                doc5 = Jsoup.connect("$urlBase" + filtro).get()
                                var filtroRegistros = doc5.select("div.filter")[1].select("a")
                                for (n in 0 until filtroRegistros.size) {
                                    if (filtroRegistros[n].text() == "Porteros") {
                                        filtro2 = filtroRegistros[n].attr("href")
                                    }
                                }
                            }

                            var partidos = doc5.select("table.min_width.sortable.stats_table.shade_zero tbody tr")
                                .stream().filter { x -> x.select("tr[class=unused_sub hidden]").isEmpty() }
                                .filter { x -> x.select("tr[class=spacer partial_table]").isEmpty() }
                                .filter { x -> x.select("tr[class=thead]").isEmpty() }.collect(Collectors.toList())

                            for (n in 0 until partidos.size) {
                                var est = EstadisticaJugador()
                                var equipoLocal =
                                    partidos[n].select("td[data-stat=squad]").text().replace("Betis", "Real Betis")
                                var equipoVisitante =
                                    partidos[n].select("td[data-stat=opponent]").text().replace("Betis", "Real Betis")

                                var partido =
                                    this.partidoServicio?.buscarPartidoPorNombresEquipos(equipoLocal, equipoVisitante)

                                var fueTitular =
                                    partidos[n].select("td[data-stat=game_started]").text().replace("*", "")
                                var minutosJTexto = partidos[n].select("td[data-stat=minutes]").text()
                                var minutosJ = 0

                                var bloqueosTexto = partidos[n].select("td[data-stat=blocks]").text()
                                var bloqueos = 0

                                var titular = false
                                if (fueTitular == "Sí") {
                                    titular = true
                                }
                                if (minutosJTexto != "") {
                                    minutosJ = minutosJTexto.toInt()
                                }
                                if (bloqueosTexto != "") {
                                    bloqueos = bloqueosTexto.toInt()
                                }
                                if (equipo == equipoLocal || equipo == equipoVisitante) {

                                    var j = this.jugadorServicio?.buscaJugadorPorNombreYEquipo(nombreJugador, equipo)
                                    est.fueTitular = titular
                                    est.minutosJugados = minutosJ
                                    est.asistencias = partidos[n].select("td[data-stat=assists]").text().toInt()
                                    est.goles = partidos[n].select("td[data-stat=goals]").text().toInt()
                                    est.penaltisLanzados = partidos[n].select("td[data-stat=pens_att]").text().toInt()
                                    est.penaltisMarcados = partidos[n].select("td[data-stat=pens_made]").text().toInt()
                                    est.disparosPuerta =
                                        partidos[n].select("td[data-stat=shots_on_target]").text().toInt()
                                    est.disparosTotales = partidos[n].select("td[data-stat=shots_total]").text().toInt()
                                    est.tarjetasAmarillas =
                                        partidos[n].select("td[data-stat=cards_yellow]").text().toInt()
                                    est.tarjetasRojas = partidos[n].select("td[data-stat=cards_red]").text().toInt()
                                    est.bloqueos = bloqueos
                                    est.robos = partidos[n].select("td[data-stat=interceptions]").text().toInt()
                                    est.jugador = j

                                    if (filtro2.isNotEmpty()) {
                                        var doc6 = Jsoup.connect("$urlBase" + filtro2).get()
                                        var partidos =
                                            doc6.select("table.min_width.sortable.stats_table.shade_zero tbody tr")
                                                .stream()
                                                .filter { x -> x.select("tr[class=unused_sub hidden]").isEmpty() }
                                                .filter { x -> x.select("tr[class=spacer partial_table]").isEmpty() }
                                                .filter { x -> x.select("tr[class=thead]").isEmpty() }
                                                .collect(Collectors.toList())
                                        est.disparosRecibidos =
                                            partidos[n].select("td[data-stat=shots_on_target_against]").text().toInt()
                                        est.golesRecibidos =
                                            partidos[n].select("td[data-stat=goals_against_gk]").text().toInt()
                                        est.salvadas = partidos[n].select("td[data-stat=saves]").text().toInt()
                                    }
                                    var puntosPorPartido = 0
                                    est.partido = partido

                                    if (est.jugador?.posicion == "PO") {
                                        puntosPorPartido += est.goles * 6
                                        puntosPorPartido += est.asistencias * 3
                                        if (est.minutosJugados > 75 && est.golesRecibidos == 0) {
                                            puntosPorPartido += 3
                                        } else if (est.minutosJugados > 60 && est.golesRecibidos == 1) {
                                            puntosPorPartido += 2
                                        }
                                        puntosPorPartido -= est.golesRecibidos
                                        if (est.disparosRecibidos > 2 && est.salvadas > 0) {
                                            var porcentajeDS =
                                                ((est.salvadas.toFloat() / est.disparosRecibidos.toFloat()))
                                            when {
                                                porcentajeDS >= 0.7 -> puntosPorPartido += 4
                                                porcentajeDS < 0.5 -> puntosPorPartido -= 2
                                            }
                                        }
                                        puntosPorPartido += est.salvadas

                                    } else if (j?.posicion == "DF") {
                                        when {
                                            est.minutosJugados > 60 && est.golesRecibidos == 0 -> puntosPorPartido += 3
                                            est.minutosJugados > 60 && est.golesRecibidos == 1 -> puntosPorPartido += 1
                                            est.minutosJugados > 60 && est.golesRecibidos > 2 -> puntosPorPartido -= est.golesRecibidos
                                        }

                                        puntosPorPartido += est.goles * 5
                                        puntosPorPartido += est.asistencias * 2
                                        puntosPorPartido += (est.robos * 0.5).toInt()
                                        puntosPorPartido += (est.bloqueos * 0.5).toInt()

                                    } else if (j?.posicion == "CC") {
                                        puntosPorPartido += est.goles * 4
                                        puntosPorPartido += est.asistencias * 3
                                        if (est.disparosTotales > 3 && est.disparosPuerta > 0) {
                                            var porcentajeTP =
                                                (est.disparosPuerta.toFloat() / est.disparosTotales.toFloat())
                                            when {
                                                porcentajeTP >= 0.5 -> puntosPorPartido += 2
                                                porcentajeTP < 0.4 -> puntosPorPartido -= 1
                                            }
                                        }
                                        puntosPorPartido += (est.robos * 0.25).toInt()
                                        puntosPorPartido += (est.bloqueos * 0.25).toInt()

                                    } else {
                                        puntosPorPartido += est.goles * 3
                                        puntosPorPartido += est.asistencias * 2
                                        if (est.disparosPuerta >= 3) {
                                            puntosPorPartido += 1
                                        }
                                        if (est.disparosTotales > 3 && est.disparosPuerta > 0) {
                                            var porcentajeTP =
                                                (est.disparosPuerta.toFloat() / est.disparosTotales.toFloat())
                                            when {
                                                porcentajeTP >= 0.7 -> puntosPorPartido += 2
                                                porcentajeTP < 0.5 -> puntosPorPartido -= 1
                                            }
                                        }
                                    }
                                    if (est.tarjetasRojas == 1) {
                                        puntosPorPartido -= est.tarjetasRojas * 3
                                    } else {
                                        est.tarjetasAmarillas * 1
                                    }
                                    est.puntos = puntosPorPartido
//                                    est.jugador?.puntos = puntosPorPartido + est.jugador?.puntos!!
//                                    j?.puntos = puntosPorPartido + j?.puntos!!
//                                    this.jugadorServicio.guardarJugador(j)
                                    guardarEstadistica(est)
                                }
                            }
                        } else {
                            println("Este jugador no tiene estadísticas de LaLiga.")
                        }
                    } else {
                        println("Este jugador no tiene estadísticas de esta temporada.")
                    }
                }
            }
    }

    fun valoraciones() {
        var urlBase = "https://es.fcstats.com/"
        var doc = Jsoup.connect("$urlBase/partidos,primera-division-espana,19,1.php").get()
        var linksPartidos =
            doc.select("table.matchesListMain tbody tr.matchRow td.matchResult a").filter { x -> x.text() != "Postp." }
                .filter { x -> x.text() != "17:00" }
        //for (n in 20 until linksPartidos.size) {
            var doc2 = Jsoup.connect("$urlBase" + linksPartidos.get(20).attr("href")).get()
            var plantilla = doc2.select("div.matchLineupsValues")
            println(linksPartidos.get(20))
            if (!plantilla.isEmpty()) {
                var equipos= doc2.select("div#pageTitle h1").text().split("-")
                var equipoLocal= equipos[0].trim()
                var equipoVisitante= equipos[1].trim()
                println(equipoLocal)
                println(equipoVisitante)
                var titularesConPuntuacion =
                    plantilla[1].select("div").filter { x -> !x.select("span.lineupRating").isEmpty() }
                if (!titularesConPuntuacion.isEmpty()) {
                    var titularesLocal = titularesConPuntuacion[1].children()
                    var nombresTL =
                        titularesLocal.stream().map { x -> x.select("a").text() }.collect(Collectors.toList())
                    var puntuacionesTL =
                        titularesLocal.stream().map { x -> x.select("span.lineupRating").text().toDouble() }
                            .collect(Collectors.toList())
                    for(n in 0 until nombresTL.size){
                        println(nombresTL[n])
                        if(this.jugadorServicio?.existeJugador(nombresTL[n], equipoLocal)==true){
                            var pId= this.partidoServicio?.buscarPartidoPorNombresEquipos(equipoLocal,equipoVisitante)?.id
                            if (pId != null) {
                               var e= this.buscarEstadisticaPorJugadorPartido(nombresTL[n], equipoLocal, pId)
                                if (e != null) {
                                    e?.puntos= e.puntos + (puntuacionesTL[n]/1.5).toInt()
                                    this.guardarEstadistica(e)
                                }
                            }
                            }
                            }
                    var titularesVis =
                        titularesConPuntuacion[13].children().filter { x -> !x.select("span.lineupRating").isEmpty() }
                    var nombresTV = titularesVis.stream().map { x -> x.select("a").text() }.collect(Collectors.toList())
                    var puntuacionesTV =
                        titularesVis.stream().map { x -> x.select("span.lineupRating").text().toDouble() }
                            .collect(Collectors.toList())

                    println("Titulares local: " + nombresTL + " " + nombresTL.size)
                    println("Puntuaciones titulares local: " + puntuacionesTL)
                    println("Titulares visitantes: " + nombresTV + " " + nombresTV.size)
                    println("Puntuación titulares visitantes: " + puntuacionesTV)
                }
            }

                var suplentes = plantilla[2].select("div")
                var nombresSLTamaño = suplentes[1].children().size
                var suplentesConPuntuacionL =
                    suplentes[1].children().filter { x -> !x.select("span.lineupRating").isEmpty() }
                var n = nombresSLTamaño
                var suplentesConPuntuacionV =
                    suplentes[n + 2].children().filter { x -> !x.select("span.lineupRating").isEmpty() }
                if (!suplentesConPuntuacionL.isEmpty()) {
                    var nombresSL =
                        suplentesConPuntuacionL.stream().map { x -> x.select("a").text() }.collect(Collectors.toList())
                    var puntuacionesSL =
                        suplentesConPuntuacionL.stream().map { x -> x.select("span.lineupRating").text().toDouble() }
                            .collect(Collectors.toList())
                    println("Suplentes local: " + nombresSL + " " + nombresSL.size)
                    println("Puntuaciones suplentes local: " + puntuacionesSL)
                }
                if (!suplentesConPuntuacionV.isEmpty()) {
                    var nombresSV =
                        suplentesConPuntuacionV.stream().map { x -> x.select("a").text() }.collect(Collectors.toList())
                    var puntuacionesSV =
                        suplentesConPuntuacionV.stream().map { x -> x.select("span.lineupRating").text().toDouble() }
                            .collect(Collectors.toList())
                    println("Suplentes visitante: " + nombresSV + " " + nombresSV.size)
                    println("Puntuaciones suplentes visitante: " + puntuacionesSV)
                }
            }
        }
    //}





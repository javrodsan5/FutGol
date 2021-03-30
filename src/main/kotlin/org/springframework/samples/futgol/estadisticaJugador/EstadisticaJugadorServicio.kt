package org.springframework.samples.futgol.estadisticaJugador

import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.samples.futgol.jugador.JugadorServicio
import org.springframework.samples.futgol.partido.PartidoServicio
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

@Service
class EstadisticaJugadorServicio {

    private var estadisticaJugadorRepositorio: EstadisticaJugadorRepositorio? = null

    @Autowired
    private val partidoServicio: PartidoServicio? = null

    @Autowired
    private val jugadorServicio: JugadorServicio? = null


    @Autowired
    fun EstadisticaJugadorServicio(estadisticaJugadorRepositorio: EstadisticaJugadorRepositorio) {
        this.estadisticaJugadorRepositorio = estadisticaJugadorRepositorio
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

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscarEstadisticasPorJugadorJornada(idJugador: Int, numeroJornada: Int): EstadisticaJugador? {
        return estadisticaJugadorRepositorio?.buscarEstadisticasPorJugadorJornada(idJugador, numeroJornada)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscarUltimaEstadistica(): EstadisticaJugador? {
        return estadisticaJugadorRepositorio?.buscarUltimaEstadistica()
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun existeAlgunaEstadistica(): Boolean {
        return estadisticaJugadorRepositorio?.existeAlgunaEstadistica()!!
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscarEstadisticasPorPartido(idPartido: Int): Collection<EstadisticaJugador>? {
        return estadisticaJugadorRepositorio?.buscarEstadisticasPorPartido(idPartido)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun existeEstadisticaJugador(nombreJugador: String, nombreEquipo: String, idPartido: Int?): Boolean? {
        return estadisticaJugadorRepositorio?.existeEstadisticaJugEqPart(nombreJugador, nombreEquipo, idPartido)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun tieneAlgunaEstadisticaJugador(idJugador: Int): Boolean? {
        return estadisticaJugadorRepositorio?.tieneAlgunaEstadisticaJugador(idJugador)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun existeEstadisticaJugadorJornada(idJugador: Int, numeroJornada: Int): Boolean? {
        return estadisticaJugadorRepositorio?.existeEstadisticaJugadorJornada(idJugador, numeroJornada)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscarEstadisticasPorJornada(jornadaId: Int): Collection<EstadisticaJugador>? {
        return estadisticaJugadorRepositorio?.buscarEstadisticasPorJornada(jornadaId)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun existeEstadisticasJornada(jornadaId: Int): Boolean? {
        return !this.buscarEstadisticasPorJornada(jornadaId).isNullOrEmpty()
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun mejorJugadorJornada(jornadaId: Int): EstadisticaJugador {
        return estadisticaJugadorRepositorio?.mejorJugadorJornada(jornadaId)?.toList()?.get(0)!!
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun jugadoresParticipantesEnJornada(jornadaId: Int): List<Jugador> {
        return estadisticaJugadorRepositorio?.jugadoresParticipantesEnJornadaMasPuntos(jornadaId)?.toList()!!
    }

    fun wsValoraciones() {
        var urlBase = "https://es.fcstats.com/"
        var doc = Jsoup.connect("$urlBase+partidos,primera-division-espana,19,1.php").get()
        var linksPartidos =
            doc.select("table.matchesListMain tbody tr.matchRow td.matchResult a").filter { x -> x.text() != "Postp." }
                .filter { x -> x.text() != "17:00" }
        var l: List<String?> = ArrayList()
        try {
            l = Files.lines(Paths.get("src/main/resources/wsFiles/CambioNombresFCstats.txt")).collect(Collectors.toList())
        } catch (e: IOException) {
            println("No se puede leer el fichero de nombres.")
        }

        for (linkPartido in linksPartidos) {
            var doc2 = Jsoup.connect("$urlBase" + linkPartido.attr("href")).get()
            var plantilla = doc2.select("div.matchLineupsValues")

            if (!plantilla.isEmpty()) {
                var equipos = doc2.select("div#pageTitle h1").text().split("-")
                var equipoLocal = equipos[0].trim()
                    .replace("FC Barcelona", "Barcelona")
                    .replace("Athletic Bilbao", "Athletic Club")
                    .replace("Celta de Vigo", "Celta Vigo")
                    .replace("Deportivo Alavés", "Alavés")
                    .replace("Real Valladolid", "Valladolid")
                var equipoVisitante = equipos[1].trim()
                    .replace("FC Barcelona", "Barcelona")
                    .replace("Athletic Bilbao", "Athletic Club")
                    .replace("Celta de Vigo", "Celta Vigo")
                    .replace("Deportivo Alavés", "Alavés")
                    .replace("Real Valladolid", "Valladolid")
                println(equipoLocal)
                println(equipoVisitante)
                if (this.partidoServicio?.existePartido(equipoLocal, equipoVisitante) == true) {
                    var partido =
                        this.partidoServicio.buscarPartidoPorNombresEquipos(equipoLocal, equipoVisitante)
                    var resultadoPartido = partido?.resultado
                    var idPartido = partido?.id
                    var num = idPartido?.let {
                        this.buscarEstadisticasPorPartido(it)?.filter { x -> x.valoracion != 0.0 }?.size
                    }
                    if (num != null) {
                        var titularesConPuntuacion =
                            plantilla[1].select("div")
                        var existeTitulares = titularesConPuntuacion.select("span").size != 0
                        if ((num < 22) && (existeTitulares) && (resultadoPartido != "")) {
                            var titularesLocal = titularesConPuntuacion[1].children()
                            var nombresTL =
                                titularesLocal.stream().map { x -> x.select("a").text() }
                                    .collect(Collectors.toList())
                            var puntuacionesTLString =
                                titularesLocal.stream().map { x -> x.select("span.lineupRating").text() }
                                    .collect(Collectors.toList())
                            var puntuacionesTL = ArrayList<Double>()
                            var puntuacion: Double
                            for (p in puntuacionesTLString) {
                                if (p != "") {
                                    puntuacion = p.toDouble()
                                    puntuacionesTL.add(puntuacion)
                                } else {
                                    puntuacionesTL.add(6.0)
                                }
                            }
                            for (r in 0 until nombresTL.size) {
                                for (element in l) {
                                    var linea = element?.split(",")
                                    if (linea?.size!! >= 3) {
                                        if (linea[2] == equipoLocal && linea[0] == nombresTL[r]
                                        ) {
                                            nombresTL.removeAt(r)
                                            nombresTL.add(r, linea[1])
                                        }
                                    } else {
                                        if (linea[0] == nombresTL[r]) {
                                            nombresTL.removeAt(r)
                                            nombresTL.add(r, linea[1])
                                        }
                                    }
                                }

                                if (this.jugadorServicio?.existeJugadorEquipo(nombresTL[r], equipoLocal) == true) {
                                    var pId =
                                        this.partidoServicio.buscarPartidoPorNombresEquipos(
                                            equipoLocal,
                                            equipoVisitante
                                        )?.id
                                    if (pId != null) {
                                        if (this.existeEstadisticaJugador(nombresTL[r], equipoLocal, pId) == true) {
                                            var e =
                                                this.buscarEstadisticaPorJugadorPartido(
                                                    nombresTL[r],
                                                    equipoLocal,
                                                    pId
                                                )
                                            if (e != null && e.valoracion == 0.0) {
                                                e.puntos = e.puntos + (puntuacionesTL[r] / 1.5).toInt()
                                                e.valoracion = puntuacionesTL[r]
                                                this.guardarEstadistica(e)
                                            }
                                        }
                                    }
                                }
                            }
                            var titularesVis =
                                titularesConPuntuacion[13].children()
                            var nombresTV =
                                titularesVis.stream().map { x -> x.select("a").text() }.collect(Collectors.toList())
                            var puntuacionesTVString =
                                titularesVis.stream().map { x -> x.select("span.lineupRating").text() }
                                    .collect(Collectors.toList())
                            var puntuacionesTV = ArrayList<Double>()

                            for (p in puntuacionesTVString) {
                                if (p != "") {
                                    puntuacion = p.toDouble()
                                    puntuacionesTV.add(puntuacion)
                                } else {
                                    puntuacionesTV.add(6.0)
                                }
                            }

                            for (s in 0 until nombresTV.size) {
                                for (element in l) {
                                    var linea = element?.split(",")
                                    if (linea?.size!! >= 3) {
                                        if (linea[2] == equipoVisitante && linea[0] == nombresTV[s]
                                        ) {
                                            nombresTV.removeAt(s)
                                            nombresTV.add(s, linea[1])
                                        }
                                    } else {
                                        if (linea[0] == nombresTV[s]) {
                                            nombresTV.removeAt(s)
                                            nombresTV.add(s, linea[1])
                                        }
                                    }
                                }

                                if (this.jugadorServicio?.existeJugadorEquipo(
                                        nombresTV[s],
                                        equipoVisitante
                                    ) == true
                                ) {
                                    var pId =
                                        this.partidoServicio.buscarPartidoPorNombresEquipos(
                                            equipoLocal,
                                            equipoVisitante
                                        )?.id
                                    if (pId != null) {
                                        if (this.existeEstadisticaJugador(
                                                nombresTV[s],
                                                equipoVisitante,
                                                pId
                                            ) == true
                                        ) {
                                            var e =
                                                this.buscarEstadisticaPorJugadorPartido(
                                                    nombresTV[s],
                                                    equipoVisitante,
                                                    pId
                                                )
                                            if (e != null && e.valoracion == 0.0) {
                                                e.puntos = e.puntos + (puntuacionesTV[s] / 1.5).toInt()
                                                e.valoracion = puntuacionesTV[s]
                                                this.guardarEstadistica(e)
                                            }
                                        }
                                    }
                                }
                            }

                            println("Titulares local: " + nombresTL + " " + nombresTL.size)
                            println("Puntuaciones titulares local: " + puntuacionesTL)
                            println("Titulares visitantes: " + nombresTV + " " + nombresTV.size)
                            println("Puntuación titulares visitantes: " + puntuacionesTV)

                            var suplentes = plantilla[2].select("div")
                            var nombresSLTamaño = suplentes[1].children().size
                            var suplentesConPuntuacionL =
                                suplentes[1].children()
                                    .filter { x -> x.select("span.lineupRating").text().isNotEmpty() }
                            var tam = nombresSLTamaño
                            var suplentesConPuntuacionV =
                                suplentes[tam + 2].children()
                                    .filter { x -> x.select("span.lineupRating").text().isNotEmpty() }
                            if (!suplentesConPuntuacionL.isEmpty()) {
                                var nombresSL =
                                    suplentesConPuntuacionL.stream().map { x -> x.select("a").text() }
                                        .collect(Collectors.toList())
                                var puntuacionesSLString =
                                    suplentesConPuntuacionL.stream().map { x -> x.select("span.lineupRating").text() }
                                        .collect(Collectors.toList())
                                var puntuacionesSL = ArrayList<Double>()
                                for (p in puntuacionesSLString) {
                                    var puntuacionSL: Double
                                    if (p != "") {
                                        puntuacionSL = p.toDouble()
                                        puntuacionesSL.add(puntuacionSL)
                                    }
                                }

                                for (p in 0 until nombresSL.size) {
                                    for (element in l) {
                                        var linea = element?.split(",")
                                        if (linea?.size!! >= 3) {
                                            if (linea[2] == equipoLocal && linea[0] == nombresSL[p]
                                            ) {
                                                nombresSL.removeAt(p)
                                                nombresSL.add(p, linea[1])
                                            }
                                        } else {
                                            if (linea[0] == nombresSL[p]) {
                                                nombresSL.removeAt(p)
                                                nombresSL.add(p, linea[1])
                                            }
                                        }
                                    }

                                    if (this.jugadorServicio?.existeJugadorEquipo(nombresSL[p], equipoLocal) == true) {
                                        var pId =
                                            this.partidoServicio.buscarPartidoPorNombresEquipos(
                                                equipoLocal,
                                                equipoVisitante
                                            )?.id
                                        if (pId != null) {
                                            if (this.existeEstadisticaJugador(nombresSL[p], equipoLocal, pId) == true) {
                                                var e =
                                                    this.buscarEstadisticaPorJugadorPartido(
                                                        nombresSL[p],
                                                        equipoLocal,
                                                        pId
                                                    )
                                                if (e != null && e.valoracion == 0.0) {
                                                    e.puntos = e.puntos + (puntuacionesSL[p] / 1.5).toInt()
                                                    e.valoracion = puntuacionesSL[p]

                                                    this.guardarEstadistica(e)
                                                }
                                            }
                                        }
                                    }
                                }
                                println("Suplentes local: " + nombresSL + " " + nombresSL.size)
                                println("Puntuaciones suplentes local: " + puntuacionesSL)
                            }
                            if (!suplentesConPuntuacionV.isEmpty()) {
                                var nombresSV =
                                    suplentesConPuntuacionV.stream().map { x -> x.select("a").text() }
                                        .collect(Collectors.toList())
                                var puntuacionesSVString =
                                    suplentesConPuntuacionV.stream().map { x -> x.select("span.lineupRating").text() }
                                        .collect(Collectors.toList())
                                var puntuacionesSV = ArrayList<Double>()
                                for (p in puntuacionesSVString) {
                                    var puntuacionSV: Double
                                    if (p != "") {
                                        puntuacionSV = p.toDouble()
                                        puntuacionesSV.add(puntuacionSV)
                                    }
                                }

                                for (f in 0 until nombresSV.size) {
                                    for (element in l) {
                                        var linea = element?.split(",")
                                        if (linea?.size!! >= 3) {
                                            if (linea[2] == equipoVisitante && linea[0] == nombresSV[f]
                                            ) {
                                                nombresSV.removeAt(f)
                                                nombresSV.add(f, linea[1])
                                            }
                                        } else {
                                            if (linea[0] == nombresSV[f]) {
                                                nombresSV.removeAt(f)
                                                nombresSV.add(f, linea[1])
                                            }
                                        }
                                    }

                                    if (this.jugadorServicio?.existeJugadorEquipo(
                                            nombresSV[f],
                                            equipoVisitante
                                        ) == true
                                    ) {
                                        var pId =
                                            this.partidoServicio.buscarPartidoPorNombresEquipos(
                                                equipoLocal,
                                                equipoVisitante
                                            )?.id
                                        if (pId != null) {
                                            if (this.existeEstadisticaJugador(
                                                    nombresSV[f],
                                                    equipoVisitante,
                                                    pId
                                                ) == true
                                            ) {
                                                var e = this.buscarEstadisticaPorJugadorPartido(
                                                    nombresSV[f],
                                                    equipoVisitante,
                                                    pId
                                                )
                                                if (e != null && e.valoracion == 0.0) {
                                                    e.puntos = e.puntos + (puntuacionesSV[f] / 1.5).toInt()
                                                    e.valoracion = puntuacionesSV[f]
                                                    this.guardarEstadistica(e)
                                                }
                                            }
                                        }
                                    }
                                }

                                println("Suplentes visitante: " + nombresSV + " " + nombresSV.size)
                                println("Puntuaciones suplentes visitante: " + puntuacionesSV)
                            }
                        }
                    }
                }
            }
        }
    }

    fun wsEstadisticas() {
        var urlBase = "https://fbref.com/"
        var doc = Jsoup.connect("$urlBase/es/comps/12/horario/Resultados-y-partidos-en-La-Liga").get()
        var partidos = doc.select("table#sched_10731_1 tbody tr")
            .filter { x -> x.select("tr.spacer.partial_table.result_all").isEmpty() }
        var l: List<String?> = ArrayList()
        try {
            l = Files.lines(Paths.get("src/main/resources/wsFiles/CambioNombresJugadores.txt")).collect(Collectors.toList())
        } catch (e: IOException) {
            println("No se puede leer el fichero de nombres.")
        }

        var ultimaEPId = 0
        if (this.existeAlgunaEstadistica()) {
            ultimaEPId = this.buscarUltimaEstadistica()?.partido?.id!!
        }
        for (partido in partidos) {
            var equipoLocal = partido.select("td[data-stat=squad_a]").text().replace("Betis", "Real Betis")
            var equipoVisitante =
                partido.select("td[data-stat=squad_b]").text().replace("Betis", "Real Betis")

            if (this.partidoServicio?.existePartido(equipoLocal, equipoVisitante) == true) {
                var p = this.partidoServicio.buscarPartidoPorNombresEquipos(equipoLocal, equipoVisitante)
                var idPartido = p?.id
                var resultadoPartido = p?.resultado
                var linkPartido = partido.select("td[data-stat=score] a").attr("href")

                if ((idPartido!! >= ultimaEPId) && (resultadoPartido != "")) {
                    var doc2 = Jsoup.connect(urlBase + linkPartido).get()
                    var alineaciones = doc2.select("div.lineup tbody")
                    var titularesLocal = alineaciones[0].select("tr").subList(1, 12)
                        .stream().map { x -> x.select("td a").text() }.collect(Collectors.toList())
                    var titularesV = alineaciones[1].select("tr").subList(1, 12)
                        .stream().map { x -> x.select("td a").text() }.collect(Collectors.toList())

                    for (element in l) {
                        var linea = element?.split(",")
                        for (j in 0 until titularesLocal.size) {
                            if (linea?.size!! >= 3) {
                                if (linea[2] == equipoLocal && linea[0] == titularesLocal[j]
                                ) {
                                    titularesLocal.removeAt(j)
                                    titularesLocal.add(j, linea[1])
                                } else if (linea[2] == equipoVisitante && linea[0] == titularesV[j]
                                ) {
                                    titularesV.removeAt(j)
                                    titularesV.add(j, linea[1])
                                }
                            } else {
                                if (linea[0] == titularesLocal[j]) {
                                    titularesLocal.removeAt(j)
                                    titularesLocal.add(j, linea[1])
                                } else if (linea[0] == titularesV[j]) {
                                    titularesV.removeAt(j)
                                    titularesV.add(j, linea[1])
                                }
                            }
                        }
                    }

                    var tablas = doc2.select("div.table_wrapper").subList(0, 4)
                    for (n in 0 until tablas.size) {
                        var equipo =
                            tablas[n].select("h2").first().text().substringBefore("Estadísticas de").trim()
                                .replace("Betis", "Real Betis")
                        var jugadores = tablas[n].select("tbody tr")
                        var tamanyo = jugadores.size
                        if (n % 2 == 0 && !tablas[n].select("div.filter.switcher").isNullOrEmpty()) {
                            tamanyo = jugadores.size / 6
                        }
                        for (i in 0 until tamanyo) {
                            var jugador = jugadores[i]
                            var nombreJugador = jugador.select("th[data-stat=player] a").text().trim()
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
                            if (jugadorServicio?.existeJugadorEquipo(nombreJugador, equipo) == true) {
                                var j =
                                    this.jugadorServicio.buscaJugadorPorNombreYEquipo(nombreJugador, equipo)
                                var est = EstadisticaJugador()
                                if (existeEstadisticaJugador(nombreJugador, equipo, idPartido) == true) {
                                    est =
                                        this.buscarEstadisticaPorJugadorPartido(
                                            nombreJugador,
                                            equipo,
                                            idPartido
                                        )!!
                                }
                                println("Nombre " + nombreJugador)
                                var puntosPorPartido = 0

                                if (n % 2 == 0) {
                                    var minutosJTexto = jugador.select("td[data-stat=minutes]").text()

                                    var minutosJ = 0
                                    if (minutosJTexto != "") {
                                        minutosJ = minutosJTexto.toInt()
                                    }
                                    est.minutosJugados = minutosJ

                                    var bloqueos = 0
                                    if (!jugador.select("td[data-stat=blocks]").isNullOrEmpty()) {
                                        var bloqueosTexto = jugador.select("td[data-stat=blocks]").text()
                                        if (bloqueosTexto != "") {
                                            bloqueos = bloqueosTexto.toInt()
                                        }
                                    }

                                    est.bloqueos = bloqueos
                                    est.asistencias = jugador.select("td[data-stat=assists]").text().toInt()
                                    est.goles = jugador.select("td[data-stat=goals]").text().toInt()
                                    est.penaltisLanzados =
                                        jugador.select("td[data-stat=pens_att]").text().toInt()
                                    est.penaltisMarcados =
                                        jugador.select("td[data-stat=pens_made]").text().toInt()
                                    est.disparosPuerta =
                                        jugador.select("td[data-stat=shots_on_target]").text().toInt()
                                    est.disparosTotales =
                                        jugador.select("td[data-stat=shots_total]").text().toInt()
                                    est.robos = jugador.select("td[data-stat=interceptions]").text().toInt()
                                    est.tarjetasAmarillas =
                                        jugador.select("td[data-stat=cards_yellow]").text().toInt()
                                    est.tarjetasRojas = jugador.select("td[data-stat=cards_red]").text().toInt()

                                    if (j?.posicion == "DF") {
                                        when {
                                            est.minutosJugados > 60 && est.golesRecibidos == 0 -> puntosPorPartido += 2
                                            est.minutosJugados > 60 && est.golesRecibidos == 1 -> puntosPorPartido += 1
                                            est.minutosJugados > 60 && est.golesRecibidos > 2 -> puntosPorPartido -= est.golesRecibidos
                                        }
                                        puntosPorPartido += est.goles * 6
                                        puntosPorPartido += est.asistencias * 2
                                        puntosPorPartido += (est.robos * 0.5).toInt()
                                        puntosPorPartido += (est.bloqueos * 0.4).toInt()

                                    } else if (j?.posicion == "CC") {
                                        puntosPorPartido += est.goles * 5
                                        puntosPorPartido += est.asistencias * 3
                                        if (est.disparosTotales > 3 && est.disparosPuerta > 0) {
                                            var porcentajeTP =
                                                (est.disparosPuerta.toFloat() / est.disparosTotales.toFloat())
                                            when {
                                                porcentajeTP >= 0.5 -> puntosPorPartido += 2
                                                porcentajeTP < 0.3 -> puntosPorPartido -= 1
                                            }
                                        }
                                        puntosPorPartido += (est.robos * 0.25).toInt()
                                        puntosPorPartido += (est.bloqueos * 0.25).toInt()

                                    } else {
                                        puntosPorPartido += est.goles * 4
                                        puntosPorPartido += est.asistencias * 2
                                        if (est.disparosPuerta >= 3) {
                                            puntosPorPartido += 1
                                        }
                                        if (est.disparosTotales > 2 && est.disparosPuerta > 0) {
                                            var porcentajeTP =
                                                (est.disparosPuerta.toFloat() / est.disparosTotales.toFloat())
                                            when {
                                                porcentajeTP >= 0.7 -> puntosPorPartido += 2
                                                porcentajeTP < 0.4 -> puntosPorPartido -= 1
                                            }
                                        }
                                    }
                                    if (est.tarjetasRojas == 1) {
                                        puntosPorPartido -= est.tarjetasRojas * 3
                                    } else {
                                        puntosPorPartido -= est.tarjetasAmarillas * 1
                                    }

                                    puntosPorPartido -= (est.penaltisLanzados - est.penaltisMarcados) * 2

                                } else {
                                    if (j?.posicion == "PO") {
                                        est =
                                            this.buscarEstadisticaPorJugadorPartido(
                                                nombreJugador,
                                                equipo,
                                                idPartido
                                            )!!
                                        est.disparosRecibidos =
                                            jugador.select("td[data-stat=shots_on_target_against]").text()
                                                .toInt()

                                        est.golesRecibidos =
                                            jugador.select("td[data-stat=goals_against_gk]").text().toInt()
                                        est.salvadas = jugador.select("td[data-stat=saves]").text().toInt()

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
                                                porcentajeDS >= 0.8 -> puntosPorPartido += 4
                                                (porcentajeDS >= 0.6 && porcentajeDS < 0.8) -> puntosPorPartido += 2
                                                porcentajeDS < 0.5 -> puntosPorPartido -= 2
                                            }
                                        }
                                        puntosPorPartido += (est.salvadas * 0.75).toInt()
                                    }
                                }

                                var titular = false
                                if (titularesLocal.contains(nombreJugador) || titularesV.contains(nombreJugador)) {
                                    titular = true
                                }

                                est.fueTitular = titular
                                est.partido = this.partidoServicio.buscarPartidoPorID(idPartido)
                                est.puntos = puntosPorPartido
                                j?.puntos = puntosPorPartido + j?.puntos!!
                                est.jugador = j

                                this.jugadorServicio.guardarJugador(j)
                                this.guardarEstadistica(est)
                            }
                        }
                    }
                }
            }
        }

    }

}







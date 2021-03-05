package org.springframework.samples.futgol.estadisticaJugador

import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.equipoReal.EquipoRealServicio
import org.springframework.samples.futgol.jugador.JugadorServicio
import org.springframework.samples.futgol.partido.PartidoServicio
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.text.Normalizer
import java.util.stream.Collectors
import kotlin.collections.ArrayList

@Service
class EstadisticaJugadorServicio {

    private var estadisticaJugadorRepositorio: EstadisticaJugadorRepositorio? = null

    @Autowired
    private val partidoServicio: PartidoServicio? = null

    @Autowired
    private val jugadorServicio: JugadorServicio? = null

    @Autowired
    private val equipoRealServicio: EquipoRealServicio? = null

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
    fun buscarUltimaEstadistica(): EstadisticaJugador? {
        return estadisticaJugadorRepositorio?.buscarUltimaEstadistica()
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscarTodasEstadisticas(): Collection<EstadisticaJugador>? {
        return estadisticaJugadorRepositorio?.findAll()
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun existeEstadisticaJugador(nombreJugador: String, nombreEquipo: String, idPartido: Int?): Boolean? {
        var res = false
        if (nombreEquipo != "" && equipoRealServicio?.existeEquipoReal(nombreEquipo) == true) {
            var estadisticas = this.buscarTodasEstadisticas()
            if (estadisticas != null || estadisticas?.isEmpty() == false) {
                for (e in estadisticas) {
                    if (e.jugador?.name == nombreJugador && e.partido?.id == idPartido && e.jugador?.club?.name == nombreEquipo) {
                        res = true
                        break
                    }
                }
            }
        }
        return res
    }

    fun valoraciones() {
        var urlBase = "https://es.fcstats.com/"
        var doc = Jsoup.connect("$urlBase/partidos,primera-division-espana,19,1.php").get()
        var linksPartidos =
            doc.select("table.matchesListMain tbody tr.matchRow td.matchResult a").filter { x -> x.text() != "Postp." }
                .filter { x -> x.text() != "17:00" }
        var l: List<String?> = ArrayList()
        try {
            l = Files.lines(Paths.get("CambioNombresJugadores.txt")).collect(Collectors.toList())
        } catch (e: IOException) {
            println("No se puede leer el fichero de nombres.")
        }
        for (n in 0 until linksPartidos.size) {
            var doc2 = Jsoup.connect("$urlBase" + linksPartidos.get(n).attr("href")).get()
            var plantilla = doc2.select("div.matchLineupsValues")

            if (!plantilla.isEmpty()) {
                var equipos = doc2.select("div#pageTitle h1").text().split("-")
                var equipoLocal = equipos[0].trim()
                    .replace("FC Barcelona","Barcelona")
                    .replace("Athletic Bilbao","Athletic Club")
                    .replace("Celta de Vigo", "Celta Vigo")
                    .replace("Deportivo Alavés", "Alavés")
                    .replace("Real Valladolid","Valladolid")
                var equipoVisitante = equipos[1].trim()
                    .replace("FC Barcelona","Barcelona")
                    .replace("Athletic Bilbao","Athletic Club")
                    .replace("Celta de Vigo", "Celta Vigo")
                    .replace("Deportivo Alavés", "Alavés")
                    .replace("Real Valladolid","Valladolid")
                println(equipoLocal)
                println(equipoVisitante)
                var titularesConPuntuacion =
                    plantilla[1].select("div").filter { x -> !x.select("span.lineupRating").isEmpty() }
                if (!titularesConPuntuacion.isEmpty()) {
                    var titularesLocal = titularesConPuntuacion[1].children()
                    var nombresTL =
                        titularesLocal.stream().map { x -> x.select("a").text() }.collect(Collectors.toList())
                    var puntuacionesTLString =
                        titularesLocal.stream().map { x -> x.select("span.lineupRating").text() }
                            .collect(Collectors.toList())
                    var puntuacionesTL= ArrayList<Double>()
                    for(p in puntuacionesTLString){
                        var puntuacion= 0.0
                        if(!p.isEmpty()){
                           puntuacion= p.toDouble()
                        }
                        puntuacionesTL.add(puntuacion)
                    }
                    for (n in 0 until nombresTL.size) {
                        for (k in 0 until l.size) {
                            var linea = l[k]?.split(",")
                            if (linea?.size!! >= 3) {
                                if (linea?.get(2).equals(equipoLocal) && linea?.get(0).equals(nombresTL[n])) {
                                    nombresTL.removeAt(n)
                                    nombresTL.add(linea?.get(1))
                                }
                            } else {
                                if (linea?.get(0).equals(nombresTL[n])) {
                                    nombresTL.removeAt(n)
                                    nombresTL.add(linea?.get(1).toString())
                                }
                            }
                        }

                        if (this.jugadorServicio?.existeJugadorEquipo(nombresTL[n], equipoLocal) == true) {
                            var pId =
                                this.partidoServicio?.buscarPartidoPorNombresEquipos(equipoLocal, equipoVisitante)?.id
                            if (pId != null) {
                                var e = this.buscarEstadisticaPorJugadorPartido(nombresTL[n], equipoLocal, pId)
                                if (e != null && e.valoracion == 0.0) {
                                    e?.puntos = e.puntos + (puntuacionesTL[n] / 1.5).toInt()
                                    e?.valoracion = puntuacionesTL[n]
                                    this.guardarEstadistica(e)
                                }
                            }
                        }
                    }
                    var titularesVis =
                        titularesConPuntuacion[13].children().filter { x -> !x.select("span.lineupRating").isEmpty() }
                    var nombresTV = titularesVis.stream().map { x -> x.select("a").text() }.collect(Collectors.toList())
                    var puntuacionesTVString =
                        titularesVis.stream().map { x -> x.select("span.lineupRating").text()}
                            .collect(Collectors.toList())
                    var puntuacionesTV= ArrayList<Double>()
                    for(p in puntuacionesTVString){
                        var puntuacion= 0.0
                        if(!p.isEmpty()){
                            puntuacion= p.toDouble()
                        }
                        puntuacionesTV.add(puntuacion)
                    }

                    for (n in 0 until nombresTV.size) {
                        for (k in 0 until l.size) {
                            var linea = l[k]?.split(",")
                            if (linea?.size!! >= 3) {
                                if (linea?.get(2).equals(equipoVisitante) && linea?.get(0).equals(nombresTV[n])) {
                                    nombresTV.removeAt(n)
                                    nombresTV.add(linea?.get(1))
                                }
                            } else {
                                if (linea?.get(0).equals(nombresTV[n])) {
                                    nombresTV.removeAt(n)
                                    nombresTV.add(linea?.get(1).toString())
                                }
                            }
                        }
                        println(nombresTV[n])
                        if (this.jugadorServicio?.existeJugadorEquipo(nombresTV[n], equipoVisitante) == true) {
                            var pId =
                                this.partidoServicio?.buscarPartidoPorNombresEquipos(equipoLocal, equipoVisitante)?.id
                            if (pId != null) {
                                var e = this.buscarEstadisticaPorJugadorPartido(nombresTV[n], equipoVisitante, pId)
                                if (e != null && e.valoracion == 0.0) {
                                    e?.puntos = e.puntos + (puntuacionesTV[n] / 1.5).toInt()
                                    e?.valoracion = puntuacionesTV[n]
                                    this.guardarEstadistica(e)
                                }
                            }
                        }
                    }

                    println("Titulares local: " + nombresTL + " " + nombresTL.size)
                    println("Puntuaciones titulares local: " + puntuacionesTL)
                    println("Titulares visitantes: " + nombresTV + " " + nombresTV.size)
                    println("Puntuación titulares visitantes: " + puntuacionesTV)
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
                    var puntuacionesSLString =
                        suplentesConPuntuacionL.stream().map { x -> x.select("span.lineupRating").text()}
                            .collect(Collectors.toList())
                    var puntuacionesSL= ArrayList<Double>()
                    for(p in puntuacionesSLString){
                        var puntuacion= 0.0
                        if(!p.isEmpty()){
                            puntuacion= p.toDouble()
                        }
                        puntuacionesSL.add(puntuacion)
                    }

                    for (n in 0 until nombresSL.size) {

                        for (k in 0 until l.size) {
                            var linea = l[k]?.split(",")
                            if (linea?.size!! >= 3) {
                                if (linea?.get(2).equals(equipoLocal) && linea?.get(0).equals(nombresSL[n])) {
                                    nombresSL.removeAt(n)
                                    nombresSL.add(linea?.get(1))
                                }
                            } else {
                                if (linea?.get(0).equals(nombresSL[n])) {
                                    nombresSL.removeAt(n)
                                    nombresSL.add(linea?.get(1).toString())
                                }
                            }
                        }

                        println(nombresSL[n])
                        if (this.jugadorServicio?.existeJugadorEquipo(nombresSL[n], equipoLocal) == true) {
                            var pId =
                                this.partidoServicio?.buscarPartidoPorNombresEquipos(equipoLocal, equipoVisitante)?.id
                            if (pId != null) {
                                var e = this.buscarEstadisticaPorJugadorPartido(nombresSL[n], equipoLocal, pId)
                                if (e != null && e.valoracion == 0.0 ) {
                                    e?.puntos = e.puntos + (puntuacionesSL[n] / 1.5).toInt()
                                    e?.valoracion = puntuacionesSL[n]

                                    this.guardarEstadistica(e)
                                }
                            }
                        }
                    }

                    println("Suplentes local: " + nombresSL + " " + nombresSL.size)
                    println("Puntuaciones suplentes local: " + puntuacionesSL)
                }
                if (!suplentesConPuntuacionV.isEmpty()) {
                    var nombresSV =
                        suplentesConPuntuacionV.stream().map { x -> x.select("a").text() }.collect(Collectors.toList())
                    var puntuacionesSVString =
                        suplentesConPuntuacionV.stream().map { x -> x.select("span.lineupRating").text() }
                            .collect(Collectors.toList())
                    var puntuacionesSV= ArrayList<Double>()
                    for(p in puntuacionesSVString){
                        var puntuacion= 0.0
                        if(!p.isEmpty()){
                            puntuacion= p.toDouble()
                        }
                        puntuacionesSV.add(puntuacion)
                    }
                    for (n in 0 until nombresSV.size) {
                        for (k in 0 until l.size) {
                            var linea = l[k]?.split(",")
                            if (linea?.size!! >= 3) {
                                if (linea?.get(2).equals(equipoVisitante) && linea?.get(0).equals(nombresSV[n])) {
                                    nombresSV.removeAt(n)
                                    nombresSV.add(linea?.get(1))
                                }
                            } else {
                                if (linea?.get(0).equals(nombresSV[n])) {
                                    nombresSV.removeAt(n)
                                    nombresSV.add(linea?.get(1).toString())
                                }
                            }
                        }
                        println(nombresSV[n])
                        if (this.jugadorServicio?.existeJugadorEquipo(nombresSV[n], equipoVisitante) == true) {
                            var pId =
                                this.partidoServicio?.buscarPartidoPorNombresEquipos(equipoLocal, equipoVisitante)?.id
                            if (pId != null) {
                                var e = this.buscarEstadisticaPorJugadorPartido(nombresSV[n], equipoVisitante, pId)
                                if (e != null && e.valoracion == 0.0) {
                                    e?.puntos = e.puntos + (puntuacionesSV[n] / 1.5).toInt()
                                    e?.valoracion = puntuacionesSV[n]
                                    this.guardarEstadistica(e)
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

    fun wsEstadisticas() {
        var urlBase = "https://fbref.com/"
        var doc = Jsoup.connect("$urlBase/es/comps/12/horario/Resultados-y-partidos-en-La-Liga").get()
        var partidos = doc.select("table#sched_ks_10731_1 tbody tr")
            .filter { x -> x.select("tr.spacer.partial_table.result_all").isEmpty() }
        var l: List<String?> = ArrayList()
        try {
            l = Files.lines(Paths.get("CambioNombresJugadores.txt")).collect(Collectors.toList())
        } catch (e: IOException) {
            println("No se puede leer el fichero de nombres.")
        }
        var estaVacioEstadisticas = this.buscarTodasEstadisticas()?.isEmpty()

        var ultimaEPId = 0
        if (estaVacioEstadisticas == false) {
            ultimaEPId = this.buscarUltimaEstadistica()?.partido?.id!!
        }

        for (n in 0 until partidos.size) {
            var equipoLocal = partidos[n].select("td[data-stat=squad_a]").text().replace("Betis", "Real Betis")
            var equipoVisitante = partidos[n].select("td[data-stat=squad_b]").text().replace("Betis", "Real Betis")

            if (this.partidoServicio?.existePartido(equipoLocal, equipoVisitante) == true) {
                var idPartido =
                    this.partidoServicio.buscarPartidoPorNombresEquipos(equipoLocal, equipoVisitante)?.id
                var linkPartido = partidos[n].select("td[data-stat=score] a").attr("href")

                if (idPartido!! >= ultimaEPId!!) {
                    var doc2 = Jsoup.connect("$urlBase" + linkPartido).get()
                    var alineaciones = doc2.select("div.lineup tbody")
                    var titularesLocal = alineaciones[0].select("tr").subList(1, 12)
                        .stream().map { x -> x.select("td a").text() }.collect(Collectors.toList())
                    var titularesV = alineaciones[1].select("tr").subList(1, 12)
                        .stream().map { x -> x.select("td a").text() }.collect(Collectors.toList())

                    for (k in 0 until l.size) {
                        var linea = l[k]?.split(",")
                        for (j in 0 until titularesLocal.size) {
                            if (linea?.size!! >= 3) {
                                if (linea?.get(2).equals(equipoLocal) && linea?.get(0).equals(titularesLocal[j])) {
                                    titularesLocal.removeAt(j)
                                    titularesLocal.add(linea?.get(1).toString())
                                } else if (linea?.get(2).equals(equipoVisitante) && linea?.get(0)
                                        .equals(titularesV[j])
                                ) {
                                    titularesV.removeAt(j)
                                    titularesV.add(linea?.get(1).toString())
                                }
                            } else {
                                if (linea?.get(0).equals(titularesLocal[j])) {
                                    titularesLocal.removeAt(j)
                                    titularesLocal.add(linea?.get(1).toString())
                                } else if (linea?.get(0).equals(titularesV[j])) {
                                    titularesV.removeAt(j)
                                    titularesV.add(linea?.get(1).toString())
                                }
                            }
                        }
                    }

                    var tablas = doc2.select("div.table_wrapper").subList(0, 4)
                    for (n in 0 until tablas.size) {
                        var equipo = tablas[n].select("h2").first().text().substringBefore("Estadísticas de").trim()
                            .replace("Betis", "Real Betis")
                        var jugadores = tablas[n].select("tbody tr")
                        var tamanyo = jugadores.size
                        if (n % 2 == 0) {
                            tamanyo = jugadores.size / 6
                        }
                        for (i in 0 until tamanyo) {
                            var jugador = jugadores[i]
                            var nombreJugador = jugador.select("th[data-stat=player] a").text().trim()
                            for (k in 0 until l.size) {
                                var linea = l[k]?.split(",")
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

                            if (jugadorServicio?.existeJugadorEquipo(nombreJugador, equipo) == true) {
                                var j = this.jugadorServicio?.buscaJugadorPorNombreYEquipo(nombreJugador, equipo)
                                var est = EstadisticaJugador()
                                if (existeEstadisticaJugador(nombreJugador, equipo, idPartido) == true) {
                                    est =
                                        this.buscarEstadisticaPorJugadorPartido(nombreJugador, equipo, idPartido)!!
                                }
                                println(nombreJugador)

                                var puntosPorPartido = 0

                                if (n % 2 == 0) {
                                    var minutosJTexto = jugador.select("td[data-stat=minutes]").text()
                                    var minutosJ = 0
                                    if (minutosJTexto != "") {
                                        minutosJ = minutosJTexto.toInt()
                                    }
                                    est.minutosJugados = minutosJ

                                    var bloqueosTexto = jugador.select("td[data-stat=blocks]").text()
                                    var bloqueos = 0
                                    if (bloqueosTexto != "") {
                                        bloqueos = bloqueosTexto.toInt()
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
                                        println(est.disparosRecibidos)
                                        est.golesRecibidos =
                                            jugador.select("td[data-stat=goals_against_gk]").text().toInt()
                                        println(est.golesRecibidos)
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
                                                porcentajeDS >= 0.7 -> puntosPorPartido += 4
                                                porcentajeDS < 0.5 -> puntosPorPartido -= 2
                                            }
                                        }
                                        puntosPorPartido += est.salvadas
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






package org.springframework.samples.futgol.liga

import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.estadisticaJugador.EstadisticaJugador
import org.springframework.samples.futgol.estadisticaJugador.EstadisticaJugadorServicio
import org.springframework.samples.futgol.partido.Partido
import org.springframework.samples.futgol.partido.PartidoServicio
import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.samples.futgol.jugador.JugadorServicio
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList
import kotlin.collections.Collection
import kotlin.collections.MutableList
import kotlin.collections.map

@Service
class LigaServicio {

    private var ligaRepositorio: LigaRepositorio? = null

    @Autowired
    private val partidoServicio: PartidoServicio? = null

    @Autowired
    private val jugadorServicio: JugadorServicio? = null

    @Autowired
    private val equipoServicio: EquipoServicio? = null

    @Autowired
    private val estadisticaJugadorServicio: EstadisticaJugadorServicio? = null

    @Autowired
    fun LigaServicio(ligaRepositorio: LigaRepositorio) {
        this.ligaRepositorio = ligaRepositorio
    }

    @Transactional()
    @Throws(DataAccessException::class)
    fun guardarLiga(liga: Liga) {
        ligaRepositorio?.save(liga)
    }

    @Transactional(readOnly = true)
    fun buscarLigaPorNombre(nombreLiga: String): Liga? {
        return ligaRepositorio?.findLigaByName((nombreLiga))
    }

    @Transactional(readOnly = true)
    fun buscarLigaPorId(idLiga: Int): Liga? {
        return ligaRepositorio?.buscarLigaPorId((idLiga))
    }

    @Transactional(readOnly = true)
    fun buscarJugadoresEnLiga(idLiga: Int): Collection<Jugador>? {
        var equipos = equipoServicio?.buscaEquiposDeLigaPorId(idLiga)
        var jugadoresLiga: MutableSet<Jugador> = HashSet()
        if (equipos != null) {
            for (e in equipos) {
                for (j in e.jugadores) {
                    if (j !in jugadoresLiga) {
                        jugadoresLiga.add(j)
                    }
                }
            }
        }
        return jugadoresLiga
    }

    @Transactional(readOnly = true)
    fun buscarJugadoresSinEquipoEnLiga(idLiga: Int): MutableSet<Jugador> {
        var todosJugadores = jugadorServicio?.buscaTodosJugadores()
        var jugadoresConEquipo = buscarJugadoresEnLiga(idLiga)
        var jugadoresSinEquipo: MutableSet<Jugador> = HashSet()
        if (todosJugadores != null) {
            for (tj in todosJugadores) {
                if (tj != null) {
                    if (!jugadoresConEquipo?.contains(tj)!!) {
                        jugadoresSinEquipo.add(tj)
                    }
                }
            }
        }
        return jugadoresSinEquipo
    }

    @Transactional(readOnly = true)
    fun comprobarSiExisteLiga(nombreLiga: String?): Boolean {
        var res = false
        var ligas = ligaRepositorio?.findAll()
        if (ligas != null) {
            for (l in ligas) {
                if (l.name.equals(nombreLiga)) {
                    res = true
                }
            }
        }
        return res
    }

    fun equiposPartidosEstadisticasJugadores() {
        var urlBase = "https://fbref.com"
        var doc = Jsoup.connect("$urlBase/es/comps/12/Estadisticas-de-La-Liga").get()
        var linksEquipos = doc.select("#results107311_overall:first-of-type tr td:first-of-type a")
            .map { col -> col.attr("href") }.stream()
            .collect(Collectors.toList()) //todos los links de los equipos de la liga
        var nombreEquipos = doc.select("#results107311_overall:first-of-type tr td:first-of-type a").text()
        print(nombreEquipos)
        var linksJug: MutableList<String> = ArrayList()
        for (linkEquipo in linksEquipos) {
            val doc2 = Jsoup.connect("$urlBase" + linkEquipo).get()

            linksJug.addAll(
                doc2.select("table.min_width.sortable.stats_table#stats_standard_10731 th a:first-of-type")
                    .map { col -> col.attr("href") }.stream().distinct().collect(Collectors.toList())
            )
            linksJug = linksJug.stream().distinct().collect(Collectors.toList()) //todos los links jugadores de la liga

        }
        var listaPartidos: MutableSet<Partido> = HashSet()
        for (linkJugador in linksJug) {
            var doc3 = Jsoup.connect("$urlBase" + linkJugador).get()
            var nombreJugador= doc3.select("h1[itemprop=name]").text()
            if (!doc3.select("table#stats_standard_dom_lg.min_width.sortable.stats_table.shade_zero tbody tr")
                    .isEmpty()
            ) {
                var ultimaTemporada =
                    doc3.select("table#stats_standard_dom_lg.min_width.sortable.stats_table.shade_zero tbody tr").last()
                var nombreUltimaTemporada = (ultimaTemporada.select("th").first().text()) //tiene que ser 2020-2021
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
                        var equipoLocal = partidos[n].select("td[data-stat=squad]").text()
                        var equipoVisitante = partidos[n].select("td[data-stat=opponent]").text()
                        var fechaPartido = partidos[n].select("th[data-stat=date] a").text()
                        var jornada = partidos[n].select("td[data-stat=round]").text()[7].toString().toInt()
                        var resultado = partidos[n].select("td[data-stat=result]").text().substring(2)
                        var fueTitular = partidos[n].select("td[data-stat=game_started]").text().replace("*", "")
                        var minutosJTexto = partidos[n].select("td[data-stat=minutes]").text()
                        var minutosJ = 0
                        var goles = partidos[n].select("td[data-stat=goals]").text().toInt()
                        var asistencias = partidos[n].select("td[data-stat=assists]").text().toInt()
                        var penaltisMar = partidos[n].select("td[data-stat=pens_made]").text().toInt()
                        var penaltisInt = partidos[n].select("td[data-stat=pens_att]").text().toInt()
                        var disparosTotal = partidos[n].select("td[data-stat=shots_total]").text().toInt()
                        var disparosPuerta = partidos[n].select("td[data-stat=shots_on_target]").text().toInt()
                        var tarjetasA = partidos[n].select("td[data-stat=cards_yellow]").text().toInt()
                        var tarjetasR = partidos[n].select("td[data-stat=cards_red]").text().toInt()
                        var robos = partidos[n].select("td[data-stat=interceptions]").text().toInt()
                        var bloqueosTexto = partidos[n].select("td[data-stat=blocks]").text()
                        var bloqueos = 0

                        //INTRODUCIR TODOS LOS PARTIDOS SIN REPETIRSE (LOS EQUIPOS Y JUGADORES TIENEN QUE ESTAR CREADOS ANTES)
                        //INTRODUCIR LAS ESTADISTICAS DE JUGADORES EN UN PARTIDO (LOS JUGADORES TIENEN QUE ESTAR CREADOS ANTES)
                        var p = Partido()
                        if (listaPartidos.stream()
                                .noneMatch { x -> x.equipoLocal?.name == equipoLocal && x.equipoVisitante?.name == equipoVisitante }
                        ) {
                            p.equipoLocal = this.equipoServicio?.buscarEquipoPorNombre(equipoLocal)
                            p.equipoVisitante = this.equipoServicio?.buscarEquipoPorNombre(equipoVisitante)
                            p.fecha = fechaPartido
                            p.jornada = jornada
                            p.resultado = resultado

                            listaPartidos.add(p)
                            this.partidoServicio?.guardarPartido(p)
                        }else{
                            p= this.partidoServicio?.buscarPartidoPorNombresEquipos(equipoLocal,equipoVisitante)!!
                        }

                        println("Equipo local: " + equipoLocal)
                        println("Equipo visitante: " + equipoVisitante)
                        println("Fecha del partido: " + fechaPartido)
                        println("Jornada: " + jornada)
                        println("Resultado: " + resultado)
                        println("------- Mis estadísticas de ese partido -------")
                        println("¿Fue titular? " + fueTitular)
                        var titular= false
                        if(fueTitular=="Sí"){
                            titular=true
                        }
                        if (minutosJTexto != "") {
                            minutosJ= minutosJTexto.toInt()
                        }
                        println("Goles: " + goles)
                        println("Asistencias: " + asistencias)
                        println("Penaltis marcados: " + penaltisMar)
                        println("Penaltis lanzados: " + penaltisInt)
                        println("Disparos totales: " + disparosTotal)
                        println("Disparos a puerta: " + disparosPuerta)
                        println("Tarjetas amarillas: " + tarjetasA)
                        println("Tarjetas rojas: " + tarjetasR)
                        println("Robos: " + robos)
                        if (bloqueosTexto != "") {
                            bloqueos= bloqueosTexto.toInt()
                        }
                        var j= this.jugadorServicio?.buscaJugadorPorNombre(nombreJugador)
                        var est= EstadisticaJugador()
                        est.fueTitular= titular
                        est.minutosJugados= minutosJ
                        est.asistencias= asistencias
                        est.goles= goles
                        est.penaltisLanzados= penaltisInt
                        est.penaltisMarcados= penaltisMar
                        est.disparosPuerta= disparosPuerta
                        est.disparosTotales= disparosTotal
                        est.tarjetasAmarillas= tarjetasA
                        est.tarjetasRojas= tarjetasR
                        est.bloqueos= bloqueos
                        est.robos= robos
                        if (!filtro2.isEmpty()) {
                            var doc6 = Jsoup.connect("$urlBase" + filtro2).get()
                            var partidos = doc6.select("table.min_width.sortable.stats_table.shade_zero tbody tr")
                                .stream().filter { x -> x.select("tr[class=unused_sub hidden]").isEmpty() }
                                .filter { x -> x.select("tr[class=spacer partial_table]").isEmpty() }
                                .filter { x -> x.select("tr[class=thead]").isEmpty() }.collect(Collectors.toList())
                            var salvadas = partidos[n].select("td[data-stat=saves]").text().toInt()
                            var disparosRecibidos =
                                partidos[n].select("td[data-stat=shots_on_target_against]").text().toInt()
                            var golesRecibidos = partidos[n].select("td[data-stat=goals_against_gk]").text().toInt()
                            est.disparosRecibidos= disparosRecibidos
                            est.golesRecibidos= golesRecibidos
                            est.salvadas= salvadas
                            println("Disparos a puerta recibidos: " + disparosRecibidos)
                            println("Salvadas: " + salvadas)
                            println("Goles recibidos: " + golesRecibidos)
                        }
                        println("---------------------------------------------")
                        est.partido= p
                        est.jugador= j
                        this.estadisticaJugadorServicio?.guardarEstadistica(est)

                    }
                } else {
                    println("Este jugador no tiene estadísticas de LaLiga.")

                }
            } else {
                println("Este jugador no tiene estadísticas de esta temporada.")
            }
        }
    }

    fun clasificacionLiga() {
        var urlBase = "https://fbref.com"
        var doc = Jsoup.connect("$urlBase/es/comps/12/Estadisticas-de-La-Liga").get()
        var datosClasificacionEq = doc.select("#results107311_overall:first-of-type tbody tr")
        for (datosEq in datosClasificacionEq) {
            var posicion = datosEq.select("th").text().toInt()
            var equipo = datosEq.select("td[data-stat=squad]").text()
            var partidosJugados = datosEq.select("td[data-stat=games]").text().toInt()
            var partidosGanados = datosEq.select("td[data-stat=wins]").text().toInt()
            var partidosEmpatados = datosEq.select("td[data-stat=draws]").text().toInt()
            var partidosPerdidos = datosEq.select("td[data-stat=losses]").text().toInt()
            var golesAFavor = datosEq.select("td[data-stat=goals_for]").text().toInt()
            var golesEnContra = datosEq.select("td[data-stat=goals_against]").text().toInt()
            var diferenciaGoles = datosEq.select("td[data-stat=goal_diff]").text().toInt()
            var puntos = datosEq.select("td[data-stat=points]").text().toInt()

            println(
                "#" + posicion + " " + equipo + " " + partidosJugados + " " + puntos + " " + partidosGanados + " " + partidosEmpatados + " "
                        + partidosPerdidos + " " + golesAFavor + " " + golesEnContra + " " + diferenciaGoles
            )

        }
    }

    fun puntuacionesPorPartido() {
        var urlBase = "https://es.fcstats.com/"
        var doc = Jsoup.connect("$urlBase/partidos,primera-division-espana,19,1.php").get()
        var linksPartidos =
            doc.select("table.matchesListMain tbody tr.matchRow td.matchResult a").filter { x -> x.text() != "Postp." }
                .filter { x -> x.text() != "17:00" }
        for (n in 0 until linksPartidos.size) {
            var doc2 = Jsoup.connect("$urlBase" + linksPartidos[n].attr("href")).get()
            var plantilla = doc2.select("div.matchLineupsValues")

            if (!plantilla.isEmpty()) {
                var titularesConPuntuacion =
                    plantilla[1].select("div").filter { x -> !x.select("span.lineupRating").isEmpty() }
                if (!titularesConPuntuacion.isEmpty()) {
                    var titularesLocal = titularesConPuntuacion[1].children()
                    var nombresTL =
                        titularesLocal.stream().map { x -> x.select("a").text() }.collect(Collectors.toList())
                    var puntuacionesTL =
                        titularesLocal.stream().map { x -> x.select("span.lineupRating").text().toDouble() }
                            .collect(Collectors.toList())
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
                } else {
                    println("No hay puntuaciones de titulares")
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
                } else {
                    println("No hay puntuaciones de suplentes locales")
                }
                if (!suplentesConPuntuacionV.isEmpty()) {
                    var nombresSV =
                        suplentesConPuntuacionV.stream().map { x -> x.select("a").text() }.collect(Collectors.toList())
                    var puntuacionesSV =
                        suplentesConPuntuacionV.stream().map { x -> x.select("span.lineupRating").text().toDouble() }
                            .collect(Collectors.toList())
                    println("Suplentes visitante: " + nombresSV + " " + nombresSV.size)
                    println("Puntuaciones suplentes visitante: " + puntuacionesSV)
                } else {
                    println("No hay puntuaciones de suplentes visitantes")

                }
            }
        }
    }

}


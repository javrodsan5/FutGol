package org.springframework.samples.futgol.liga

import org.apache.tomcat.util.http.parser.HttpParser.isNumeric
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.usuario.UsuarioServicio
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors

@Service
class LigaServicio {

    private var ligaRepositorio: LigaRepositorio? = null

    @Autowired
    private val usuarioServicio: UsuarioServicio? = null

    @Autowired
    fun LigaServicio(ligaRepositorio: LigaRepositorio) {
        this.ligaRepositorio = ligaRepositorio
    }

    @Transactional()
    @Throws(DataAccessException::class)
    fun saveLiga(liga: Liga) {
        ligaRepositorio?.save(liga)
    }

    @Transactional(readOnly = true)
    fun findLigaByName(nombreLiga: String): Liga? {
        return ligaRepositorio?.findLigaByName((nombreLiga))
    }

    @Transactional(readOnly = true)
    fun buscarLigaPorId(idLiga: Int): Liga? {
        return ligaRepositorio?.buscarLigaPorId((idLiga))
    }

    @Transactional(readOnly = true)
    fun buscarUsuario(idLiga: Int): Liga? {
        return ligaRepositorio?.buscarLigaPorId((idLiga))
    }

    @Transactional(readOnly = true)
    fun checkLigaExists(nombreLiga: String?): Boolean {
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

    fun equiposYEstadisticasJugadores() {
        val urlBase = "https://fbref.com"
        val doc = Jsoup.connect("$urlBase/es/comps/12/Estadisticas-de-La-Liga").get()
        val linksEquipos = doc.select("#results107311_overall:first-of-type tr td:first-of-type a")
            .map { col -> col.attr("href") }.stream()
            .collect(Collectors.toList()) //todos los links de los equipos de la liga
        var linksJug: MutableList<String> = ArrayList()
        for (linkEquipo in linksEquipos) {
            val doc2 = Jsoup.connect("$urlBase" + linkEquipo).get()
            //linkPartidosJugador= doc2.select("table.min_width.sortable.stats_table#stats_standard_10731 td.left.group_start a").attr("href"))
            linksJug.addAll(
                doc2.select("table.min_width.sortable.stats_table#stats_standard_10731 th a:first-of-type")
                    .map { col -> col.attr("href") }.stream().distinct().collect(Collectors.toList())
            )
            linksJug = linksJug.stream().distinct().collect(Collectors.toList()) //todos los links jugadores de la liga

        }
        for(linkJugador in linksJug){
            println(linkJugador)
            val doc3 = Jsoup.connect("$urlBase" + linkJugador).get()
            if(!doc3.select("table#stats_standard_dom_lg.min_width.sortable.stats_table.shade_zero tbody tr").isEmpty()) {
                var ultimaTemporada =
                    doc3.select("table#stats_standard_dom_lg.min_width.sortable.stats_table.shade_zero tbody tr").last()
                var nombreUltimaTemporada = (ultimaTemporada.select("th").first().text()) //tiene que ser 2020-2021
                var ligaUltimaTemporada = ultimaTemporada.select("td")[3].text()
                var partidosUltimaTemporada = ultimaTemporada.select("td").last().select("a").attr("href")
                if (nombreUltimaTemporada.equals("2020-2021") && ligaUltimaTemporada.contains("La Liga")) { //h
                    val doc4 = Jsoup.connect("$urlBase" + partidosUltimaTemporada).get()
                    var filtroCompeticiones= doc4.select("div.filter").first().select("a")
                    var filtro = ""
                    for(n in 0 until filtroCompeticiones.size){
                        if(filtroCompeticiones[n].text()=="2020-2021 La Liga"){
                            filtro = filtroCompeticiones[n].attr("href")
                        }
                    }
                    val doc5 = Jsoup.connect("$urlBase" + filtro).get()
                    var partidos= doc5.select("table.min_width.sortable.stats_table.shade_zero tbody tr")

                    var filtroRegistros= doc4.select("div.filter")[1].select("a")

                    var filtro2=""
                    for(n in 0 until filtroRegistros.size){
                        if(filtroRegistros[n].text()=="Porteros"){
                            filtro2 = filtroCompeticiones[n].attr("href")
                        }
                    }

                    for(n in 0 until partidos.size){
                        println("Equipo local: " + partidos[n].select("td[data-stat=squad]").text())
                        println("Equipo visitante: " + partidos[n].select("td[data-stat=opponent]").text())
                        println("Fecha del partido: " + partidos[n].select("th[data-stat=date] a").text())
                        println("Jornada: " + partidos[n].select("td[data-stat=round]").text())
                        println("Resultado: " + partidos[n].select("td[data-stat=result]").text())

                        println("------- Mis estadísticas de ese partido -------")
                        println("Minutos jugados: " + partidos[n].select("td[data-stat=minutes]").text())
                        println("Goles: " + partidos[n].select("td[data-stat=goals]").text())
                        println("Asistencias: " + partidos[n].select("td[data-stat=assists]").text())
                        println("Penaltis marcados: " + partidos[n].select("td[data-stat=pens_made]").text())
                        println("Penaltis lanzados: " + partidos[n].select("td[data-stat=pens_att]").text())
                        println("Disparos totales: " + partidos[n].select("td[data-stat=shots_total]").text())
                        println("Disparos a puerta: " + partidos[n].select("td[data-stat=shots_on_target]").text())
                        println("Tarjetas amarillas: " + partidos[n].select("td[data-stat=cards_yellow]").text())
                        println("Tarjetas rojas: " + partidos[n].select("td[data-stat=cards_red]").text())
                        println("Robos: " + partidos[n].select("td[data-stat=interceptions]").text())
                        println("Bloqueos: " + partidos[n].select("td[data-stat=blocks]").text())

                        println("---------------------------------------------")
                    }

                }
            }else{
                println("Este jugador no tiene estadísticas de esta temporada.")
            }
        }
    }

    fun precioEstadoYFoto() {// tambien coger estado del jugador: lesionado, en forma...
        val urlBase = "https://www.transfermarkt.es"
        val doc = Jsoup.connect("$urlBase/laliga/startseite/wettbewerb/ES1/plus/?saison_id=2020").get()
        val linksEquipos= doc.select("div.box.tab-print td a.vereinprofil_tooltip").map { col -> col.attr("href") }.stream().distinct().collect(Collectors.toList())
        for (linkEquipo in linksEquipos){
            val doc2 = Jsoup.connect("$urlBase"+linkEquipo).get()
            val linkPlantilla= doc2.select("li#vista-general.first-button a").map { col -> col.attr("href") }.stream().distinct().collect(Collectors.toList())
            val doc3 = Jsoup.connect("$urlBase"+linkPlantilla[0]).get()
            val precioJugadores= doc3.select("div.responsive-table tbody tr td.rechts.hauptlink")
            val jugadores= doc3.select("div#yw1.grid-view table.items tbody tr:first-of-type")
            jugadores.removeAt(0)
            for(n in 0 until jugadores.size){
                var persona= jugadores[n].select("td div.di.nowrap:first-of-type span a")
                var nombre = persona.attr("title")
                var linkDetallePersona = persona.attr("href")
                val doc4 = Jsoup.connect("$urlBase"+linkDetallePersona).get()
                var foto= doc4.select("div.dataBild img").attr("src")
                var estado= "En forma"
                if(!jugadores[n].select("td span.verletzt-table").isEmpty()){
                    estado= "Lesionado"
                }else if(!jugadores[n].select("td span.ausfall-6-table").isEmpty()){
                    estado= "Sancionado/No disponible"
                }
                var precio= precioJugadores[n].text()
                var precioD =0.1
                if(!precio.isEmpty()) {
                    if(precio.contains("mill")){
                        precioD = precio.substringBefore(" mil").replace(",", ".").toDouble()
                    }else{
                        precioD = precio.substringBefore(" mil").replace(",", ".").toDouble()/1000//pasar a millones
                    }
                }
                println(nombre)
                println(precioD)
                println(estado)
                println(foto)

            }
        }
    }

    fun clasificacionLiga() {
        var urlBase = "https://fbref.com"
        var doc = Jsoup.connect("$urlBase/es/comps/12/Estadisticas-de-La-Liga").get()
        var datosClasificacionEq = doc.select("#results107311_overall:first-of-type tbody tr")
        for(datosEq in datosClasificacionEq){
            var posicion= datosEq.select("th").text().toInt()
            var equipo = datosEq.select("td[data-stat=squad]").text()
            var partidosJugados = datosEq.select("td[data-stat=games]").text().toInt()
            var partidosGanados = datosEq.select("td[data-stat=wins]").text().toInt()
            var partidosEmpatados = datosEq.select("td[data-stat=draws]").text().toInt()
            var partidosPerdidos = datosEq.select("td[data-stat=losses]").text().toInt()
            var golesAFavor = datosEq.select("td[data-stat=goals_for]").text().toInt()
            var golesEnContra = datosEq.select("td[data-stat=goals_against]").text().toInt()
            var diferenciaGoles = datosEq.select("td[data-stat=goal_diff]").text().toInt()
            var puntos = datosEq.select("td[data-stat=points]").text().toInt()

            println("#" + posicion + " " + equipo + " " + partidosJugados + " " + puntos + " " + partidosGanados + " " + partidosEmpatados + " "
                    + partidosPerdidos + " " + golesAFavor + " " + golesEnContra + " " + diferenciaGoles)

        }
    }


}

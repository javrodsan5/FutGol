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

    //de momento este será su sitio hasta que se cree el servicio de jugador
    fun equiposYJugadores() {
        val urlBase = "https://fbref.com"
        val doc = Jsoup.connect("$urlBase/es/comps/12/Estadisticas-de-La-Liga").get()
        val linksEquipos = doc.select("#results107311_overall:first-of-type tr td:first-of-type a")
            .map { col -> col.attr("href") }.stream()
            .collect(Collectors.toList()) //todos los links de los equipos de la liga
        val nombreEquipos = doc.select("#results107311_overall:first-of-type tr td:first-of-type a").text()
        print(nombreEquipos)
        var linksJug: MutableList<String> = ArrayList()
        for (linkEquipo in linksEquipos) {
            val doc2 = Jsoup.connect("$urlBase" + linkEquipo).get()
            linksJug.addAll(doc2.select("table.min_width.sortable.stats_table#stats_standard_10731 th a:first-of-type")
                .map { col -> col.attr("href") }.stream().distinct().collect(Collectors.toList())
            )
            linksJug = linksJug.stream().distinct().collect(Collectors.toList()) //todos los links jugadores de la liga

        }
        for (n in 0 until linksJug.size - 1) {
            val doc3 = Jsoup.connect("$urlBase" + linksJug[n]).get()
            println(linksJug[n]) //link jugador
            var foto: String
            if (doc3.select("div.players#info div#meta img").map { col -> col.attr("src") }.size != 0) {
                foto = doc3.select("div.players#info div#meta img").map { col -> col.attr("src") }[0] //foto jugador
                println(foto)
            } else {
                foto = "No dispone de foto."
            }
            var element: Elements? = doc3.select("div.players#info div#meta p")
            var posicion: String
            var pie: String
            var altura: Double
            var peso: Double
            var nacimiento: String
            var club: String
            for (n in 0 until element?.size!!) {
                if (element[n].text().contains("Posición:")) {
                    posicion = element[n].text().split("▪")[0].substringAfter(": ")
                    println(posicion)
                }
                if (element[n].text().contains("Pie primario:")) {
                    pie = if (element[n].text().contains("%")) {
                        element[n].text().split("▪")[1].substringAfter("% ").replace("*", "") //pie primario

                    } else {
                        element[n].text().split("▪")[1].substringAfter(": ") //pie primario
                    }
                    println(pie)

                }
                if (element[n].text().contains("cm") && isNumeric(element[n].text()[0].toInt())) {
                    altura = element[n].text().split(",")[0].substringBefore("cm").trim().toDouble()
                    println(altura)

                }
                if (element[n].text().contains("kg") && isNumeric(element[n].text()[0].toInt())) {
                    peso = element[n].text().split(",")[1].substringBefore("kg").trim().toDouble()
                    println(peso)

                }
                if (element[n].text().contains("Nacimiento:")) {
                    nacimiento = element[n].text().substringAfter("Nacimiento: ")
                    println(nacimiento)

                }
                if (element[n].text().contains("Club")) {
                    club = element[n].text().substringAfter("Club : ").trim()
                    println(club)
                }
            }
        }

    }

    fun precio() {// tambien coger estado del jugador: lesionado, en forma...
        val urlBase = "https://www.transfermarkt.es"
        val doc = Jsoup.connect("$urlBase/laliga/startseite/wettbewerb/ES1/plus/?saison_id=2020").get()
        val linksEquipos =
            doc.select("div.box.tab-print td a.vereinprofil_tooltip").map { col -> col.attr("href") }.stream()
                .distinct().collect(Collectors.toList())
        for (linkEquipo in linksEquipos) {
            val doc2 = Jsoup.connect("$urlBase" + linkEquipo).get()
            val linkPlantilla =
                doc2.select("li#vista-general.first-button a").map { col -> col.attr("href") }.stream().distinct()
                    .collect(Collectors.toList())
            val doc3 = Jsoup.connect("$urlBase" + linkPlantilla[0]).get()
            val nombresJugadores = doc3.select("div.responsive-table tbody tr td div.di.nowrap:first-of-type span a")
            val precioJugadores = doc3.select("div.responsive-table tbody tr td.rechts.hauptlink")
            for (n in 0 until nombresJugadores.size) {
                println((nombresJugadores[n].attr("title")))
                var precio = (precioJugadores[n]).text()
                var precioD = 0.0
                if (precio.isNotEmpty()) {
                    precioD = if (precio.contains("mill")) {
                        precio.substringBefore(" mil").replace(",", ".").toDouble()
                    } else {
                        precio.substringBefore(" mil").replace(",", ".").toDouble() / 1000//pasar a millones
                    }
                }
                println(precioD)

            }
        }
    }

}

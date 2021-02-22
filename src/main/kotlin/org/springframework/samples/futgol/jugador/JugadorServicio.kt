package org.springframework.samples.futgol.jugador

import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.equipoReal.EquipoRealServicio
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
    fun existeJugador(nombreJugador: String): Boolean? {
        var res = false
        var jugadores = jugadorRepositorio?.findAll()
        if (jugadores != null) {
            for (j in jugadores) {
                if (j.name == nombreJugador) {
                    res = true
                    break
                }
            }
        }
        return res
    }

    fun webScrapingJugadores() {
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
                    .replace("Real Betis Balompié", "Betis").replace("Deportivo", "")
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

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscaTodosJugadores(): Collection<Jugador>? {
        return jugadorRepositorio?.findAll()
    }
}

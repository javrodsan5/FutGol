package org.springframework.samples.futgol.jugador

import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.equipo.Equipo
import org.springframework.samples.futgol.equipo.EquipoRepositorio
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors

@Service
class JugadorServicio {

    private var jugadorRepositorio: JugadorRepositorio? = null

    @Autowired
    fun JugadorServicio(jugadorRepositorio: JugadorRepositorio) {
        this.jugadorRepositorio = jugadorRepositorio
    }

    @Transactional()
    @Throws(DataAccessException::class)
    fun saveJugador(jugador: Jugador) {
        jugadorRepositorio?.save(jugador)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscaJugadorPorId(idJugador: Int): Jugador? {
        return jugadorRepositorio?.findById(idJugador)
    }

    fun webScrapingJugadores() {
        var urlBase = "https://www.transfermarkt.es"
        var doc = Jsoup.connect("$urlBase/laliga/startseite/wettbewerb/ES1/plus/?saison_id=2020").get()
        var linksEquipos =
            doc.select("div.box.tab-print td a.vereinprofil_tooltip").map { col -> col.attr("href") }.stream()
                .distinct().collect(Collectors.toList())
        for (linkEquipo in linksEquipos) {
            var doc2 = Jsoup.connect("$urlBase" + linkEquipo).get()
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
                    if (precio.contains("mill")) {
                        precioD = precio.substringBefore(" mil").replace(",", ".").toDouble()
                    } else {
                        precioD = precio.substringBefore(" mil").replace(",", ".").toDouble() / 1000//pasar a millones
                    }
                }
                jugador.name = nombre
                jugador.estadoLesion = estado
                jugador.valor = precioD
                jugador.foto = foto
                saveJugador(jugador)
            }
        }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscaTodosJugadores(): Collection<Jugador>? {
        return jugadorRepositorio?.findAll()
    }
}

package org.springframework.samples.futgol.liga

import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.samples.futgol.jugador.JugadorServicio
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.stream.Collectors

@Service
class LigaServicio {

    private var ligaRepositorio: LigaRepositorio? = null

    @Autowired
    private val jugadorServicio: JugadorServicio? = null

    @Autowired
    private val equipoServicio: EquipoServicio? = null

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


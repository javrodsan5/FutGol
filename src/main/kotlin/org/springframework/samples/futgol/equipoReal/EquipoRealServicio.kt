package org.springframework.samples.futgol.equipoReal

import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.jornadas.JornadaServicio
import org.springframework.samples.futgol.partido.Partido
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EquipoRealServicio {

    private var equipoRealRepositorio: EquipoRealRepositorio? = null

    @Autowired
    private var equipoRealServicio: EquipoRealServicio? = null

    @Autowired
    private var jornadaServicio: JornadaServicio? = null

    @Autowired
    fun EquipoServicio(equipoRealRepositorio: EquipoRealRepositorio) {
        this.equipoRealRepositorio = equipoRealRepositorio
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun existeEquipoReal(nombreEquipo: String): Boolean? {
        var res = false
        var equipos = equipoRealRepositorio?.findAll()
        if (equipos != null) {
            for (e in equipos) {
                if (e.name == nombreEquipo) {
                    res = true
                    break
                }
            }
        }
        return res
    }

    @Transactional()
    @Throws(DataAccessException::class)
    fun guardarEquipo(equipo: EquipoReal) {
        equipoRealRepositorio?.save(equipo)
    }

    @Transactional(readOnly = true)
    fun buscarTodosEquiposReales(): Collection<EquipoReal>? {
        return equipoRealRepositorio?.findAll()
    }

    @Transactional(readOnly = true)
    fun proximoPartido(nombreEquipo: String): Partido {
        var equipoReal = buscarEquipoRealPorNombre(nombreEquipo)!!
        var ultJornadaJugada = 0

        var pl = equipoReal.partidosLocal.sortedBy { x -> x.jornada?.numeroJornada }.findLast { x -> x.resultado != "" }
        var pv =
            equipoReal.partidosVisitante.sortedBy { x -> x.jornada?.numeroJornada }.findLast { x -> x.resultado != "" }
        if (pl!!.jornada?.numeroJornada!! > pv!!.jornada?.numeroJornada!!)
            ultJornadaJugada = pl.jornada?.numeroJornada!! + 1
        else {
            ultJornadaJugada = pv.jornada?.numeroJornada!! + 1
        }
        print("jornada: " + ultJornadaJugada)
        if (ultJornadaJugada < 38) {
            var jornada = jornadaServicio?.buscarJornadaPorNumeroJornada(ultJornadaJugada)
            //jornada.partidos está vacío
            for (p in jornada!!.partidos) {
                if (p.equipoLocal?.name == nombreEquipo || p.equipoVisitante?.name == nombreEquipo) {
                    print(p.equipoLocal?.name)

                    return p
                }
            }
        }
        return Partido()
    }

    @Transactional(readOnly = true)
    fun buscaPartidoJornada(numeroJornada: Int, nombreEquipo: String): Partido? {
        var jornada = jornadaServicio?.buscarJornadaPorNumeroJornada(numeroJornada)
        for (p in jornada!!.partidos) {
            if (p.equipoLocal?.name == nombreEquipo || p.equipoVisitante?.name == nombreEquipo) {
                return p
            }
        }
        return Partido()
    }

    @Transactional(readOnly = true)
    fun buscarEquipoRealPorNombre(nombre: String): EquipoReal? {
        return equipoRealRepositorio?.buscarEquipoRealPorNombre(nombre)
    }

    @Transactional()
    fun webScrapingEquipos() {
        var urlBase = "https://fbref.com/"
        var doc = Jsoup.connect("$urlBase/es/comps/12/Estadisticas-de-La-Liga").get()
        var nombres = doc.select("#results107311_overall:first-of-type tbody tr")
        for (n in 0 until nombres.size) {
            var nombreEquipo = nombres[n].select("td:first-of-type a").text().replace("Betis", "Real Betis")
            var equipo = EquipoReal()
            if (this.existeEquipoReal(nombreEquipo) == true) {
                equipo = this.buscarEquipoRealPorNombre(nombreEquipo)!!

            } else {
                equipo.name = nombreEquipo
                var linkEquipo = nombres[n].select("td:first-of-type a").attr("href")
                var doc2 = Jsoup.connect("$urlBase" + linkEquipo).get()
                equipo.escudo = doc2.select("div.media-item.logo img").attr("src")
            }
            equipo.posicion = nombres[n].select("th").text().toInt()
            equipo.partidosJugados = nombres[n].select("td[data-stat=games]").text().toInt()
            equipo.partidosGanados = nombres[n].select("td[data-stat=wins]").text().toInt()
            equipo.partidosEmpatados = nombres[n].select("td[data-stat=draws]").text().toInt()
            equipo.partidosPerdidos = nombres[n].select("td[data-stat=losses]").text().toInt()
            equipo.golesAFavor = nombres[n].select("td[data-stat=goals_for]").text().toInt()
            equipo.golesEnContra = nombres[n].select("td[data-stat=goals_against]").text().toInt()
            equipo.diferenciaGoles = nombres[n].select("td[data-stat=goal_diff]").text().toInt()
            equipo.puntos = nombres[n].select("td[data-stat=points]").text().toInt()
            this.guardarEquipo(equipo)
        }
    }
}

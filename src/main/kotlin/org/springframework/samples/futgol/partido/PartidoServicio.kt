package org.springframework.samples.futgol.partido

import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.equipoReal.EquipoRealServicio
import org.springframework.samples.futgol.jornadas.JornadaServicio
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PartidoServicio {

    private var partidoRepositorio: PartidoRepositorio? = null

    @Autowired
    private val equipoRealServicio: EquipoRealServicio? = null

    @Autowired
    private val jornadaServicio: JornadaServicio? = null

    @Autowired
    fun LigaServicio(partidoRepositorio: PartidoRepositorio) {
        this.partidoRepositorio = partidoRepositorio
    }

    @Transactional
    @Throws(DataAccessException::class)
    fun guardarPartido(partido: Partido) {
        partidoRepositorio?.save(partido)
    }

    @Transactional(readOnly = true)
    fun buscarTodosPartidos(): Collection<Partido>? {
        return partidoRepositorio?.findAll()
    }

    @Transactional(readOnly = true)
    fun buscarPartidoPorID(id: Int): Partido? {
        return partidoRepositorio?.findById(id)
    }

    @Transactional(readOnly = true)
    fun buscarPartidoPorNombresEquipos(equipoLocal: String, equipoVisitante: String): Partido? {
        return partidoRepositorio?.buscarPartidoPorNombresEquipos(equipoLocal, equipoVisitante)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun existePartido(equipoLocal: String, equipoVisitante: String): Boolean? {
        var res = false
        var partidos = this.buscarTodosPartidos()
        if (partidos != null) {
            for (p in partidos) {
                if (p.equipoLocal?.name == equipoLocal && p.equipoVisitante?.name == equipoVisitante) {
                    res = true
                    break
                }
            }
        }
        return res
    }

    @Transactional
    fun wsPartidos() {
        var urlBase = "https://fbref.com/"
        var doc = Jsoup.connect("$urlBase/es/comps/12/horario/Resultados-y-partidos-en-La-Liga").get()
        var partidos = doc.select("table#sched_10731_1 tbody tr")
            .filter { x -> x.select("tr.spacer.partial_table.result_all").isEmpty() }
        for (partido in partidos) {
            var p = Partido()
            var equipoLocal = partido.select("td[data-stat=squad_a]").text().replace("Betis", "Real Betis")
            p.equipoLocal = this.equipoRealServicio?.buscarEquipoRealPorNombre(equipoLocal)
            var equipoVisitante = partido.select("td[data-stat=squad_b]").text().replace("Betis", "Real Betis")
            p.equipoVisitante = this.equipoRealServicio?.buscarEquipoRealPorNombre(equipoVisitante)

            var resultadoTexto = partido.select("td[data-stat=score] a").text()

            if (this.existePartido(equipoLocal, equipoVisitante) == false) {
                p.fecha = partido.select("td[data-stat=date] a").text()
                p.jornada = jornadaServicio?.buscarJornadaPorNumeroJornada(
                    partido.select("th[data-stat=gameweek]").text().toInt()
                )
                p.resultado = resultadoTexto
                this.guardarPartido(p)
            } else {
                var pa = this.buscarPartidoPorNombresEquipos(equipoLocal, equipoVisitante)!!
                if (pa.resultado == "" && resultadoTexto != "") {
                    pa.resultado = resultadoTexto
                    this.guardarPartido(pa)
                }

            }
        }
    }
}

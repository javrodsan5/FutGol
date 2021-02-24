package org.springframework.samples.futgol.partido

import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.equipoReal.EquipoRealServicio
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PartidoServicio {

    private var partidoRepositorio: PartidoRepositorio? = null

    @Autowired
    private val equipoRealServicio: EquipoRealServicio? = null

    @Autowired
    fun LigaServicio(partidoRepositorio: PartidoRepositorio) {
        this.partidoRepositorio = partidoRepositorio
    }

    @Transactional()
    @Throws(DataAccessException::class)
    fun guardarPartido(partido: Partido) {
        partidoRepositorio?.save(partido)
    }


    @Transactional(readOnly = true)
    fun buscarPartidoPorNombresEquipos(equipoLocal: String, equipoVisitante: String): Partido? {
        return partidoRepositorio?.buscarPartidoPorNombresEquipos(equipoLocal, equipoVisitante)
    }

    @Transactional
    fun wsPartidos() {
        var urlBase = "https://fbref.com/"
        var doc = Jsoup.connect("$urlBase/es/comps/12/horario/Resultados-y-partidos-en-La-Liga").get()
        var partidos = doc.select("table#sched_ks_10731_1 tbody tr")
            .filter { x -> x.select("tr.spacer.partial_table.result_all").isEmpty() }
        for (partido in partidos) {
            var p = Partido()
            var equipoLocal = partido.select("td[data-stat=squad_a]").text().replace("Betis", "Real Betis")
                .replace("C�diz", "Cádiz")
                .replace("Atl�tico Madrid", "Atlético Madrid")
                .replace("Alav�s","Alavés")
            println(equipoLocal)
            p.equipoLocal = this.equipoRealServicio?.buscarEquipoRealPorNombre(equipoLocal)
            var equipoVisitante = partido.select("td[data-stat=squad_b]").text().replace("Betis", "Real Betis")
                .replace("C�diz", "Cádiz")
                .replace("Atl�tico Madrid", "Atlético Madrid")
                .replace("Alav�s","Alavés")
            println(equipoVisitante)
            p.equipoVisitante = this.equipoRealServicio?.buscarEquipoRealPorNombre(equipoVisitante)
            p.fecha = partido.select("td[data-stat=date] a").text()
            //p.jornada = partido.select("th[data-stat=gameweek]").text().toInt()
            p.resultado = partido.select("td[data-stat=score] a").text()
            this.guardarPartido(p)
        }
    }
}

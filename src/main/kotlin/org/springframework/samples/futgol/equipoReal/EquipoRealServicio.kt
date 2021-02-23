package org.springframework.samples.futgol.equipoReal

import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.equipo.Equipo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EquipoRealServicio {

    private var equipoRealRepositorio: EquipoRealRepositorio? = null

    @Autowired
    fun EquipoServicio(equipoRealRepositorio: EquipoRealRepositorio) {
        this.equipoRealRepositorio = equipoRealRepositorio
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
    fun buscarEquipoRealPorNombre(nombre: String): EquipoReal? {
        return equipoRealRepositorio?.buscarEquipoRealPorNombre(nombre)
    }
    @Transactional()
    fun webScrapingEquipos(){
    var urlBase = "https://fbref.com/"
    var doc = Jsoup.connect("$urlBase/es/comps/12/Estadisticas-de-La-Liga").get()
    var nombres = doc.select("#results107311_overall:first-of-type tbody tr")
    for (n in 0 until nombres.size) {
        var equipo = EquipoReal()
        equipo.posicion = nombres[n].select("th").text().toInt()
        equipo.partidosJugados = nombres[n].select("td[data-stat=games]").text().toInt()
        equipo.partidosGanados = nombres[n].select("td[data-stat=wins]").text().toInt()
        equipo.partidosEmpatados = nombres[n].select("td[data-stat=draws]").text().toInt()
        equipo.partidosPerdidos = nombres[n].select("td[data-stat=losses]").text().toInt()
        equipo.golesAFavor = nombres[n].select("td[data-stat=goals_for]").text().toInt()
        equipo.golesEnContra = nombres[n].select("td[data-stat=goals_against]").text().toInt()
        equipo.diferenciaGoles = nombres[n].select("td[data-stat=goal_diff]").text().toInt()
        equipo.puntos = nombres[n].select("td[data-stat=points]").text().toInt()
        equipo.name = nombres[n].select("td:first-of-type a").text()

        var linkEquipo = nombres[n].select("td:first-of-type a").attr("href")
        var doc2 = Jsoup.connect("$urlBase" + linkEquipo).get()
        equipo.escudo = doc2.select("div.media-item.logo img").attr("src")

        this.guardarEquipo(equipo)
    }
    }
}
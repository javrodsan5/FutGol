package org.springframework.samples.futgol.equipo

import org.jsoup.Jsoup
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EquipoServicio {

    private var equipoRepositorio: EquipoRepositorio? = null

    @Transactional()
    @Throws(DataAccessException::class)
    fun saveEquipo(equipo: Equipo) {
        equipoRepositorio?.save(equipo)
    }

    fun guardarEquipos(): Int {
        val doc = Jsoup.connect("https://fbref.com/es/comps/12/Estadisticas-de-La-Liga").get()
        val nombres = doc.select("#results107311_overall:first-of-type tbody tr")
        var numEquipos = 0
        for (n in 0 until nombres.size) {
            var nombre = nombres[n].select("td:first-of-type a").text()
            var equipo = Equipo()
            equipo.name = nombre
            saveEquipo(equipo)
            numEquipos += 1
        }
        return numEquipos
    }
}

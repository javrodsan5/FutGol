package org.springframework.samples.futgol.partido

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.collections.Collection

@Service
class PartidoServicio {

    private var partidoRepositorio: PartidoRepositorio? = null

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
    fun buscarTodosPartidos(): Collection<Partido>? {
        return partidoRepositorio?.findAll()
    }

    @Transactional(readOnly = true)
    fun buscarPartidoPorNombresEquipos(equipoLocal: String, equipoVisitante:String): Partido? {
        return partidoRepositorio?.buscarPartidoPorNombresEquipos(equipoLocal, equipoVisitante)
    }
}

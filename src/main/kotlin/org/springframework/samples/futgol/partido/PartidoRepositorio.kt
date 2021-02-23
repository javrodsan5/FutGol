package org.springframework.samples.futgol.partido

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository

interface PartidoRepositorio : Repository<Partido, Int> {

    fun findAll(): Collection<Partido>

    fun save(partido: Partido)

    @Query("SELECT p FROM Partido p where p.equipoLocal.name = ?1 AND p.equipoVisitante.name = ?2")
    fun buscarPartidoPorNombresEquipos(equipoLocal: String, equipoVisitante:String): Partido

}

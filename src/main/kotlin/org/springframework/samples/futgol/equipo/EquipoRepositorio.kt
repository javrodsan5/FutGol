package org.springframework.samples.futgol.equipo

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.samples.futgol.liga.Liga

interface EquipoRepositorio: Repository<Equipo, Int> {

    fun findAll(): Collection<Equipo>

    fun save(equipo: Equipo)

    @Query("SELECT e FROM Equipo e where e.liga.id = ?1")
    fun buscarEquiposDeLigaPorId(id: Int): Collection<Equipo>

}

package org.springframework.samples.futgol.equipoReal

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.samples.futgol.equipo.Equipo

interface EquipoRealRepositorio: Repository<EquipoReal, Int> {

    fun findAll(): Collection<EquipoReal>

    fun save(equipo: EquipoReal)

    @Query("SELECT e FROM EquipoReal e where e.name = ?1")
    fun buscarEquipoRealPorNombre(nombre: String): EquipoReal
}

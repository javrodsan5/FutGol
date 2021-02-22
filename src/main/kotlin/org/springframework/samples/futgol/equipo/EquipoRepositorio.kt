package org.springframework.samples.futgol.equipo

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository

interface EquipoRepositorio : Repository<Equipo, Int> {

    fun findAll(): Collection<Equipo>

    fun save(equipo: Equipo)

    @Query("SELECT e FROM Equipo e where e.liga.id = ?1")
    fun buscarEquiposDeLigaPorId(id: Int): Collection<Equipo>

    @Query("SELECT e FROM Equipo e where e.id = ?1")
    fun buscaEquiposPorId(id: Int): Equipo


    //REVISAR
    @Query("SELECT e FROM Equipo e where e.name = ?1")
    fun buscarEquipoPorNombre(nombre: String): Equipo

}

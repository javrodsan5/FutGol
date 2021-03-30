package org.springframework.samples.futgol.equipo

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository

interface EquipoRepositorio : Repository<Equipo, Int> {

    fun findAll(): Collection<Equipo>

    fun save(equipo: Equipo)

    @Query("SELECT l.equipos FROM Liga l where l.id = ?1")
    fun buscarEquiposDeLigaPorId(id: Int): Collection<Equipo>

    @Query("SELECT e FROM Equipo e where e.id = ?1")
    fun buscaEquiposPorId(id: Int): Equipo

    //REVISAR
    @Query("SELECT e FROM Equipo e where e.name = ?1")
    fun buscarEquipoPorNombre(nombre: String): Equipo

    @Query("SELECT e FROM Equipo e where e.name = ?1 and e.liga.id = ?2")
    fun buscarEquipoPorNombreYLiga(nombreEquipo: String, idLiga: Int): Equipo

    @Query("SELECT e FROM Equipo e where e.usuario.user.username = ?1 AND e.liga.id = ?2")
    fun buscarEquipoUsuarioEnLiga(nombreUsuario: String, idLiga: Int): Equipo

    @Query(value = "SELECT CASE WHEN count(e)> 0 THEN TRUE ELSE FALSE END FROM Equipo e where e.usuario.user.username = ?1 AND e.liga.id = ?2")
    fun existeUsuarioConEquipoEnLiga(nombreUsuario: String, idLiga: Int): Boolean

    @Query(value = "SELECT CASE WHEN count(e)> 0 THEN TRUE ELSE FALSE END FROM Equipo e where e.name = ?1 AND e.liga.id = ?2")
    fun existeEquipoEnLiga(nombreEquipo: String, idLiga: Int): Boolean

    @Query(value = "SELECT CASE WHEN count(e)> 0 THEN TRUE ELSE FALSE END FROM Equipo e where e.id = ?1")
    fun existeEquipo(idEquipo: Int): Boolean


}

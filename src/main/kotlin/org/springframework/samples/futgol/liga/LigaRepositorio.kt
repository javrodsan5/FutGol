package org.springframework.samples.futgol.liga

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository

interface LigaRepositorio : Repository<Liga, Int> {

    fun findAll(): Collection<Liga>

    fun save(liga: Liga)

    fun findLigaByName(nombre: String): Liga

    @Query("SELECT l FROM Liga l where l.id = ?1")
    fun buscarLigaPorId(id: Int): Liga

    @Query(value = "SELECT CASE WHEN count(l)> 0 THEN TRUE ELSE FALSE END FROM Liga l where l.name = ?1")
    fun comprobarSiExisteLiga(nombre: String): Boolean

    @Query(value = "SELECT CASE WHEN count(l)> 0 THEN TRUE ELSE FALSE END FROM Liga l where l.id = ?1")
    fun comprobarSiExisteLiga2(idLiga: Int): Boolean


}

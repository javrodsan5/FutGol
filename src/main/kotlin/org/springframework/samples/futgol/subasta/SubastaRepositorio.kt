package org.springframework.samples.futgol.subasta

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository

interface SubastaRepositorio : Repository<Subasta, Int> {

    fun save(subasta: Subasta)

    fun removeSubastaByLigaId(idLiga: Int)

    @Query("SELECT s FROM Subasta s where s.liga.id = ?1")
    fun findSubastaByLigaId(id: Int): Subasta

    @Query(value = "SELECT CASE WHEN count(s)> 0 THEN TRUE ELSE FALSE END FROM Subasta s where s.liga.id = ?1")
    fun existeSubastaLiga(id: Int): Boolean
}

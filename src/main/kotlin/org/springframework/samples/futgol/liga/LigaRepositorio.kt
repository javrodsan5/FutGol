package org.springframework.samples.futgol.liga

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.samples.futgol.usuario.Usuario
import org.springframework.transaction.annotation.Transactional

interface LigaRepositorio : Repository<Liga, Int> {

    fun findAll(): Collection<Liga>

    fun save(liga: Liga)

    fun findLigaByName(nombre : String): Liga

    @Query("SELECT l FROM Liga l where l.id = ?1")
    fun buscarLigaPorId(id: Int): Liga
}

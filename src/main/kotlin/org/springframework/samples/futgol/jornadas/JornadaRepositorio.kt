package org.springframework.samples.futgol.jornadas

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository

interface JornadaRepositorio : Repository<Jornada, Int> {

    fun findAll(): Collection<Jornada>

    fun save(jornada: Jornada)

    fun findJornadaById(id: Int): Jornada

    fun findJornadaByNumeroJornada(id: Int): Jornada

    @Query(value = "SELECT CASE WHEN count(j)> 0 THEN TRUE ELSE FALSE END FROM Jornada j where j.numeroJornada = ?1")
    fun existeJornada(numeroJornada: Int): Boolean
}

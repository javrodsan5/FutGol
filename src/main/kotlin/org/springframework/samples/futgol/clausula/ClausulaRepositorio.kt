package org.springframework.samples.futgol.clausula

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.samples.futgol.jugador.Jugador

interface ClausulaRepositorio : Repository<Clausula, Int> {

    fun save(clausula: Clausula)

    @Query("SELECT c FROM Clausula c where c.jugador.id = ?1")
    fun findClausulasByJugadorId(idJugador: Int): Collection<Clausula>

    fun findClausulaById(idClausula: Int): Clausula
}

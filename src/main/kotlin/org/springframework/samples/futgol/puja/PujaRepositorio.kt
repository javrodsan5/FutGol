package org.springframework.samples.futgol.puja

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository

interface PujaRepositorio : Repository<Puja, Int> {


    fun save(puja: Puja)

    fun findPujaById(id: Int): Puja

    @Modifying
    @Query("DELETE FROM Puja p WHERE p.equipo.id = ?1 AND p.jugador.id = ?2")
    fun removePujaByEquipoIdAndJugadorId(idEquipo: Int, idJugador: Int)

    @Query("SELECT CASE WHEN count(p)> 0 THEN TRUE ELSE FALSE END FROM Puja p where p.equipo.id = ?1 AND p.jugador.id = ?2")
    fun existePujaEquipo(idEquipo: Int, idJugador: Int): Boolean
}

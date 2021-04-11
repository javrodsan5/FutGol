package org.springframework.samples.futgol.puja

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.samples.futgol.liga.Liga

interface PujaRepositorio : Repository<Puja, Int> {


    fun save(puja: Puja)

    @Modifying
    @Query("DELETE FROM Puja p WHERE p.equipo.id = ?1 AND p.jugador.id = ?2 AND p.subasta.id = ?3")
    fun removePujaByEquipoJugadorSubasta(idEquipo: Int, idJugador: Int, idSubasta: Int)

    @Query("SELECT CASE WHEN count(p)> 0 THEN TRUE ELSE FALSE END FROM Puja p where p.equipo.id = ?1 AND p.jugador.id = ?2 AND p.subasta.id = ?3")
    fun existePujaEqJugSub(idEquipo: Int, idJugador: Int, idSubasta: Int): Boolean

    @Query("SELECT CASE WHEN count(p)> 0 THEN TRUE ELSE FALSE END FROM Puja p where p.jugador.id = ?1 AND p.subasta.id = ?2")
    fun existePujaEqJug(idJugador: Int, idSubasta: Int): Boolean

    @Query("SELECT p FROM Puja p where p.jugador.id = ?1 AND p.equipo.liga.id = ?2")
    fun buscarPujasJugadorLiga(idJugador: Int, idLiga: Int): Collection<Puja>
}

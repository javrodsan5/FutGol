package org.springframework.samples.futgol.intercambio

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository

interface IntercambioRepositorio : Repository<Intercambio, Int> {

    fun save(intercambio: Intercambio)

    @Query("SELECT i FROM Intercambio i where i.equipoCreadorIntercambio.usuario.id = ?1")
    fun buscarIntercambiosPorIdUsuario(idUsuario: Int): Collection<Intercambio>

    @Query(value = "SELECT CASE WHEN count(i)> 0 THEN TRUE ELSE FALSE END FROM Intercambio i where i.equipoCreadorIntercambio.usuario.id = ?1")
    fun existenIntercambiosPorIdUsuario(idUsuario: Int): Boolean

    @Modifying
    @Query("DELETE FROM Intercambio i WHERE i.id = ?1")
    fun removeIntercambioById(idIntercambio: Int)

    @Query("SELECT i FROM Intercambio i where i.id = ?1")
    fun buscaIntercambioPorId(id: Int): Intercambio
}

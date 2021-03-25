package org.springframework.samples.futgol.movimientos

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository

interface MovimientoRepositorio : Repository<Movimiento, Int> {


    @Query("SELECT m FROM Movimiento m where m.liga.id = ?1")
    fun buscarMovimientosDeLigaPorId(idLiga: Int): Collection<Movimiento>

    @Query("SELECT m FROM Movimiento m where m.creadorMovimiento.user.username = ?1")
    fun buscarMovimientosPorUsuario(nombreUsuario: String): Collection<Movimiento>

}

package org.springframework.samples.futgol.administracion

import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.equipoReal.EquipoRealServicio
import org.springframework.samples.futgol.estadisticaJugador.EstadisticaJugadorServicio
import org.springframework.samples.futgol.jugador.JugadorServicio
import org.springframework.samples.futgol.liga.LigaServicio
import org.springframework.samples.futgol.partido.PartidoServicio
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Controller

@EnableScheduling
@Controller
class AdministracionControlador(
    val estadisticaJugadorServicio: EstadisticaJugadorServicio,
    val partidoServicio: PartidoServicio,
    val jugadorServicio: JugadorServicio,
    val ligaServicio: LigaServicio,
    val equipoServicio: EquipoServicio,
    val equipoRealServicio: EquipoRealServicio
) {

    @Scheduled(cron = "0 0 2 * * THU ")
    fun administrarWebScraping() {
        this.equipoRealServicio.webScrapingEquipos()
        this.partidoServicio.wsPartidos()
        this.jugadorServicio.webScrapingJugadoresTransfermarkt()
        this.jugadorServicio.webScrapingJugadoresFbref()
        this.jugadorServicio.eliminarJugadoresSinPosicionFbref()
        this.estadisticaJugadorServicio.wsEstadisticas()
        this.estadisticaJugadorServicio.wsValoraciones()
        this.equipoServicio.asignarPuntosEquipo()
    }
}

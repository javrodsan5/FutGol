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
import java.time.LocalDateTime

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

    //@Scheduled(cron = "0 0 1 * * MON ")
    @Scheduled(cron = "0 38 19 * * ?")
    fun administrarWebScraping(): String {
        this.equipoRealServicio.webScrapingEquipos()
        this.partidoServicio.wsPartidos()
        this.jugadorServicio.webScrapingJugadoresTransfermarkt()
        this.jugadorServicio.webScrapingJugadoresFbref()
        this.estadisticaJugadorServicio.wsEstadisticas()
        this.estadisticaJugadorServicio.wsValoraciones()
        this.asignarPuntosEquipo()
        println(LocalDateTime.now())

        return "welcome"
    }


    fun asignarPuntosEquipo() {
        var ligas = this.ligaServicio.buscarTodasLigas()!!
        for (l in ligas) {
            var equipos = l.id?.let { this.equipoServicio.buscaEquiposDeLigaPorId(it) }
            if (equipos != null) {
                for (e in equipos) {
                    e.name?.let { equipoServicio.asignaPuntosEquipo(it, 15) }
                }
            }
        }
    }
}

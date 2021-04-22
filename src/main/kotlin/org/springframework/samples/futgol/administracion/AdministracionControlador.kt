package org.springframework.samples.futgol.administracion

import org.springframework.samples.futgol.equipo.Equipo
import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.equipoReal.EquipoRealServicio
import org.springframework.samples.futgol.estadisticaJugador.EstadisticaJugadorServicio
import org.springframework.samples.futgol.jugador.JugadorServicio
import org.springframework.samples.futgol.liga.LigaServicio
import org.springframework.samples.futgol.partido.PartidoServicio
import org.springframework.samples.futgol.subasta.SubastaServicio
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@EnableScheduling
@Controller
class AdministracionControlador(
    val estadisticaJugadorServicio: EstadisticaJugadorServicio,
    val partidoServicio: PartidoServicio,
    val jugadorServicio: JugadorServicio,
    val ligaServicio: LigaServicio,
    val equipoServicio: EquipoServicio,
    val equipoRealServicio: EquipoRealServicio,
    val subastaServicio: SubastaServicio
) {

    @Scheduled(cron = "0 0 1 * * MON ")
    fun administrarWebScrapingSemanal() {
        this.equipoRealServicio.webScrapingEquipos()
        this.partidoServicio.wsPartidos()
        this.estadisticaJugadorServicio.wsEstadisticas()
        this.estadisticaJugadorServicio.wsValoraciones()
        this.equipoServicio.asignarPuntosEquipo()
    }

    @Scheduled(cron = "0 0 1 * * ? ")
    fun administrarWebScrapingDiario() {
        this.jugadorServicio.webScrapingJugadoresTransfermarkt()
        this.jugadorServicio.webScrapingJugadoresFbref()
        this.jugadorServicio.eliminarJugadoresSinPosicionFbref()

    }

    @Scheduled(cron = "0 0 1 * * ? ")
    fun autoSubastas() {
        this.subastaServicio.autoGanaryGenerarSubasta()
    }

    @GetMapping("/pujaAuto")
    fun pujaAuto(): String {
        this.subastaServicio.autoGanaryGenerarSubasta()
        return "welcome"
    }

    @GetMapping("/puntos")
    fun puntosAuto(): String {
        this.equipoServicio.asignarPuntosEquipo()
        return "welcome"
    }

    @Scheduled(cron = "40 12 20 * * ? ")
    fun costeClausulasJornada() {
        val ligas = ligaServicio.buscarTodasLigas()!!
        for (l in ligas) {
            for (e in l.equipos) {
                for(j in e.jugadores) {
                    e.dineroRestante-= (j.valor*4000).toInt()
                }
                equipoServicio.guardarEquipo(e)
            }
        }
    }


}

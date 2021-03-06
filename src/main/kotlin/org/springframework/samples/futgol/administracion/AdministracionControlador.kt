package org.springframework.samples.futgol.administracion

import org.springframework.samples.futgol.clausula.ClausulaServicio
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
    val subastaServicio: SubastaServicio,
    val clausulaServicio: ClausulaServicio
) {

    @Scheduled(cron = "0 0 4 * * MON ")
    fun administrarWebScrapingSemanal() {
        this.equipoRealServicio.webScrapingEquipos()
        this.partidoServicio.wsPartidos()
        this.estadisticaJugadorServicio.wsEstadisticas()
        this.estadisticaJugadorServicio.wsValoraciones()
        this.equipoServicio.asignarPuntosEquipo()
        costeClausulasJornada()
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


    fun costeClausulasJornada() {
        val ligas = ligaServicio.buscarTodasLigas()!!
        for (l in ligas) {
            for (e in l.equipos) {
                for(j in e.jugadores) {
                    var clau =clausulaServicio.buscarClausulasPorJugadorYEquipo(j.id!!, e.id!!)!!
                    e.dineroRestante-= (clau.valorClausula*0.01).toInt()
                }
                equipoServicio.guardarEquipo(e)
            }
        }
    }

    @GetMapping("/aboutUs")
    fun aboutUs(): String {
        return "aboutUs"
    }

}

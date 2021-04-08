package org.springframework.samples.futgol.estadisticaJugador

import lombok.Getter
import lombok.Setter
import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.samples.futgol.model.BaseEntity
import org.springframework.samples.futgol.partido.Partido
import javax.persistence.*

@Getter
@Setter
@Entity
@Table(name = "estadisticas_jugadores")
class EstadisticaJugador : BaseEntity() {

    @Column(name = "fue_titular")
    var fueTitular = false

    @Column(name = "minutos_jugados")
    var minutosJugados = 0

    @Column(name = "goles")
    var goles = 0

    @Column(name = "asistencias")
    var asistencias = 0

    @Column(name = "penaltis_marcados")
    var penaltisMarcados = 0

    @Column(name = "penaltis_lanzados")
    var penaltisLanzados = 0

    @Column(name = "disparos_puerta")
    var disparosPuerta = 0

    @Column(name = "disparos_totales")
    var disparosTotales = 0

    @Column(name = "tarjetas_amarillas")
    var tarjetasAmarillas = 0

    @Column(name = "tarjetas_rojas")
    var tarjetasRojas = 0

    @Column(name = "robos")
    var robos = 0

    @Column(name = "bloqueos")
    var bloqueos = 0

    @Column(name = "puntos")
    var puntos = 0

    @Column(name = "valoracion")
    var valoracion = 0.0

    //portero--
    @Column(name = "disparos_recibidos")
    var disparosRecibidos = 0

    @Column(name = "goles_recibidos")
    var golesRecibidos = 0

    @Column(name = "salvadas")
    var salvadas = 0
    //--

    @ManyToOne()
    @JoinColumn(name = "jugador", referencedColumnName = "id")
    var jugador: Jugador? = null

    @ManyToOne()
    @JoinColumn(name = "partido", referencedColumnName = "id")
    var partido: Partido? = null
}

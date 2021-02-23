package org.springframework.samples.futgol.jugador

import lombok.Getter
import lombok.Setter
import org.springframework.samples.futgol.equipo.Equipo
import org.springframework.samples.futgol.equipoReal.EquipoReal
import org.springframework.samples.futgol.estadisticaJugador.EstadisticaJugador
import org.springframework.samples.futgol.model.NamedEntity
import java.util.*
import javax.persistence.*

@Getter
@Setter
@Entity
@Table(name = "jugadores")
class Jugador : NamedEntity() {


    @Column(name = "valor")
    var valor = 0.0

    @Column(name = "puntos")
    var puntos = 0

    @Column(name = "foto")
    var foto = ""

    @Column(name = "estado_lesion")
    var estadoLesion = ""

    @ManyToOne()
    @JoinColumn(name = "club", referencedColumnName = "id")
    var club: EquipoReal? = null

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "equipo_jugadores",
        joinColumns = [JoinColumn(name = "equipo_id")],
        inverseJoinColumns = [JoinColumn(name = "jugador_id")]
    )
    var equipos: MutableSet<Equipo> = HashSet()

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "jugador")
    var estadisticas: MutableSet<EstadisticaJugador> = HashSet()

}

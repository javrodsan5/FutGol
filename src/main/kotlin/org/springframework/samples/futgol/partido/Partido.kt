package org.springframework.samples.futgol.partido

import lombok.Getter
import lombok.Setter
import org.springframework.samples.futgol.equipoReal.EquipoReal
import org.springframework.samples.futgol.estadisticaJugador.EstadisticaJugador
import org.springframework.samples.futgol.jornadas.Jornada
import org.springframework.samples.futgol.model.BaseEntity
import java.util.*
import javax.persistence.*

@Getter
@Setter
@Entity
@Table(name = "partidos")
class Partido() : BaseEntity() {

    @ManyToOne()
    @JoinColumn(name = "equipo_local", referencedColumnName = "id")
    var equipoLocal: EquipoReal? = null

    @ManyToOne()
    @JoinColumn(name = "equipo_visitante", referencedColumnName = "id")
    var equipoVisitante: EquipoReal? = null

    @ManyToOne()
    @JoinColumn(name = "jornada", referencedColumnName = "numero_jornada")
    var jornada: Jornada? = null

    @Column(name = "fecha")
    var fecha = ""

    @Column(name = "resultado")
    var resultado = ""

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "partido")
    var estadisticasJugador: MutableSet<EstadisticaJugador> = HashSet()

}

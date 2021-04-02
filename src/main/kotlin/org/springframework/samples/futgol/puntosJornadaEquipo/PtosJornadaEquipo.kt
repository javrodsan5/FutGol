package org.springframework.samples.futgol.puntosJornadaEquipo

import lombok.Getter
import lombok.Setter
import org.springframework.samples.futgol.equipo.Equipo
import org.springframework.samples.futgol.jornadas.Jornada
import org.springframework.samples.futgol.model.BaseEntity
import javax.persistence.*


@Getter
@Setter
@Entity
@Table(name = "puntosjornadaequipo")
class PtosJornadaEquipo : BaseEntity(){

    @ManyToOne
    @JoinColumn(name = "jornada", referencedColumnName = "numero_jornada")
    var jornada: Jornada? = null

    @ManyToOne
    @JoinColumn(name = "equipo", referencedColumnName = "id")
    var equipo: Equipo? = null

    @Column(name = "puntos")
    var puntos = 0

}

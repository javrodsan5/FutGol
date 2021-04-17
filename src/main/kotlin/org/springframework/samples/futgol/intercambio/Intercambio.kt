package org.springframework.samples.futgol.intercambio

import lombok.Getter
import lombok.Setter
import org.springframework.samples.futgol.equipo.Equipo
import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.samples.futgol.model.BaseEntity
import javax.persistence.*

@Getter
@Setter
@Entity
@Table(name = "intercambios")
class Intercambio: BaseEntity() {

    @ManyToOne
    var equipoCreadorIntercambio: Equipo? = null

    @ManyToOne
    var otroEquipo: Equipo? = null

    @ManyToOne
    var jugadorCreadorIntercambio: Jugador? = null

    @ManyToOne
    var otroJugador: Jugador? = null

    @Column(name = "dinero")
    var dinero = 0

}

package org.springframework.samples.futgol.movimientos

import lombok.Getter
import lombok.Setter
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.samples.futgol.liga.Liga
import org.springframework.samples.futgol.model.BaseEntity
import org.springframework.samples.futgol.usuario.Usuario
import java.util.*
import javax.persistence.*

@Getter
@Setter
@Entity
@Table(name = "movimientos")
class Movimiento : BaseEntity() {

    @ManyToOne()
    @JoinColumn(name = "liga", referencedColumnName = "id")
    var liga: Liga? = null

    @ManyToOne()
    @JoinColumn(name = "creador_movimiento", referencedColumnName = "username")
    var creadorMovimiento: Usuario? = null

    @ManyToOne()
    @JoinColumn(name = "jugador", referencedColumnName = "name")
    var jugador: Jugador? = null

}
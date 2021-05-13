package org.springframework.samples.futgol.clausula

import lombok.Getter
import lombok.Setter
import org.springframework.samples.futgol.equipo.Equipo
import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.samples.futgol.model.BaseEntity
import java.util.*
import javax.persistence.*

@Getter
@Setter
@Entity
@Table(name = "clausulas")
class Clausula : BaseEntity() {

    @Column(name = "valor_clausula")
    var valorClausula = 0

    @ManyToOne()
    @JoinColumn(name = "jugador", referencedColumnName = "id")
    var jugador: Jugador? = null

    @ManyToOne()
    @JoinColumn(name = "equipo", referencedColumnName = "id")
    var equipo: Equipo? = null

    @Column(name = "ult_modificacion")
    var ultModificacion: Date = Date()
}

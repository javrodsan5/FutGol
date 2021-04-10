package org.springframework.samples.futgol.puja

import lombok.Getter
import lombok.Setter
import org.springframework.samples.futgol.equipo.Equipo
import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.samples.futgol.model.BaseEntity
import org.springframework.samples.futgol.subasta.Subasta
import javax.persistence.*

@Getter
@Setter
@Entity
@Table(name = "pujas")
class Puja : BaseEntity() {

    @ManyToOne
    @JoinColumn(name = "subasta", referencedColumnName = "id")
    var subasta: Subasta? = null

    @Column(name = "cantidad")
    var cantidad = 0

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "jugador", referencedColumnName = "id")
    var jugador: Jugador? = null

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "equipo", referencedColumnName = "id")
    var equipo: Equipo? = null
}

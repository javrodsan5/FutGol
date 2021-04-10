package org.springframework.samples.futgol.subasta

import lombok.Getter
import lombok.Setter
import org.springframework.samples.futgol.jornadas.Jornada
import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.samples.futgol.liga.Liga
import org.springframework.samples.futgol.model.BaseEntity
import java.util.HashSet
import javax.persistence.*

@Getter
@Setter
@Entity
@Table(name = "subasta")
class Subasta: BaseEntity() {

    @ManyToOne
    @JoinColumn(name = "liga", referencedColumnName = "id")
    var liga: Liga? = null

    @ManyToMany
    @JoinTable(
        name = "subasta_jugadores",
        joinColumns = [JoinColumn(name = "subasta_id")],
        inverseJoinColumns = [JoinColumn(name = "jugador_id")]
    )
    var jugadores: MutableList<Jugador> = ArrayList()





}

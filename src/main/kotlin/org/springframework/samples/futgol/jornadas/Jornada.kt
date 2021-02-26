package org.springframework.samples.futgol.jornadas

import lombok.Getter
import lombok.Setter
import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.samples.futgol.model.BaseEntity
import org.springframework.samples.futgol.partido.Partido
import java.util.*
import javax.persistence.*

@Getter
@Setter
@Entity
@Table(name = "jornadas")
class Jornada : BaseEntity() {


    @Column(name = "numero_jornada")
    var numeroJornada = 0

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "id")
    var partidos: MutableSet<Partido> = HashSet()

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "jornadas_jugadores",
        joinColumns = [JoinColumn(name = "numero_jornada")],
        inverseJoinColumns = [JoinColumn(name = "jugador_id")]
    )
    var jugadores: MutableSet<Jugador> = HashSet()

}
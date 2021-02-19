package org.springframework.samples.futgol.equipo

import lombok.Getter
import lombok.Setter
import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.samples.futgol.liga.Liga
import org.springframework.samples.futgol.login.User
import org.springframework.samples.futgol.model.NamedEntity
import java.util.*
import javax.persistence.*

@Getter
@Setter
@Entity
@Table(name = "equipos")
class Equipo() : NamedEntity() {

    @ManyToOne()
    @JoinColumn(name = "liga", referencedColumnName = "id")
    var liga: Liga? = null

    @Column(name = "puntos")
    var puntos = 0

    @Column(name = "dinero_restante")
    var dineroRestante = 0

    @ManyToOne()
    @JoinColumn(name = "usuario", referencedColumnName = "username")
    var usuario: User? = null

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "equipo_jugBanquillo",
        joinColumns = [JoinColumn(name = "jugador_id")],
        inverseJoinColumns = [JoinColumn(name = "equipo_id")]
    )
    var jugBanquillo: MutableSet<Jugador> = HashSet()

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "equipo_onceInicial",
        joinColumns = [JoinColumn(name = "jugador_id")],
        inverseJoinColumns = [JoinColumn(name = "equipo_id")]
    )
    var onceInicial: MutableSet<Jugador> = HashSet()

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "equipo_jugadores",
        joinColumns = [JoinColumn(name = "jugador_id")],
        inverseJoinColumns = [JoinColumn(name = "equipo_id")]
    )
    var jugadores: MutableSet<Jugador> = HashSet()
}
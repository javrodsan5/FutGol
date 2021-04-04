package org.springframework.samples.futgol.equipo

import lombok.Getter
import lombok.Setter
import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.samples.futgol.liga.Liga
import org.springframework.samples.futgol.model.NamedEntity
import org.springframework.samples.futgol.usuario.Usuario
import java.util.*
import javax.persistence.*
import kotlin.collections.ArrayList

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

    @Column(name = "formacion")
    var formacion = ""

    @ManyToOne()
    @JoinColumn(name = "usuario", referencedColumnName = "username")
    var usuario: Usuario? = null

    @ManyToMany()
    @JoinTable(
        name = "equipo_jugBanquillo",
        joinColumns = [JoinColumn(name = "equipo_id")],
        inverseJoinColumns = [JoinColumn(name = "jugador_id")]
    )
    var jugBanquillo: MutableList<Jugador> = ArrayList()


    @ManyToMany()
    @JoinTable(
        name = "equipo_onceInicial",
        joinColumns = [JoinColumn(name = "equipo_id")],
        inverseJoinColumns = [JoinColumn(name = "jugador_id")]
    )
    var onceInicial: MutableList<Jugador> = ArrayList()

    @ManyToMany()
    @JoinTable(
        name = "equipo_jugadores",
        joinColumns = [JoinColumn(name = "equipo_id")],
        inverseJoinColumns = [JoinColumn(name = "jugador_id")]
    )
    var jugadores: MutableSet<Jugador> = HashSet()

}

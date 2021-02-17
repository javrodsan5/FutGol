package org.springframework.samples.futgol.equipo

import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.samples.futgol.liga.Liga
import org.springframework.samples.futgol.model.NamedEntity
import org.springframework.samples.futgol.usuario.Usuario
import java.util.HashSet
import javax.persistence.*
import javax.validation.constraints.NotNull

class Equipo: NamedEntity() {

    @ManyToOne()
    @JoinColumn(name = "liga", referencedColumnName = "liga")
    var liga: Liga? = null

    @Column(name = "puntos")
    @NotNull
    var puntos = 0

    @Column(name = "dineroRestante")
    @NotNull
    var dineroRestante = 0

    @ManyToOne()
    @JoinColumn(name = "usuario", referencedColumnName = "usuario")
    var usuario: Usuario? = null

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "equipo_jugBanquillo", joinColumns = [JoinColumn(name = "jugador_id")], inverseJoinColumns = [JoinColumn(name = "equipo_id")])
    var jugBanquillo: MutableSet<Jugador> = HashSet()

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "equipo_onceInicial", joinColumns = [JoinColumn(name = "jugador_id")], inverseJoinColumns = [JoinColumn(name = "equipo_id")])
    var onceInicial: MutableSet<Jugador> = HashSet()

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "equipo_jugadores", joinColumns = [JoinColumn(name = "jugador_id")], inverseJoinColumns = [JoinColumn(name = "equipo_id")])
    var jugadores: MutableSet<Jugador> = HashSet()
}

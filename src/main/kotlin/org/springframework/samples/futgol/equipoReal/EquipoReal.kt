package org.springframework.samples.futgol.equipoReal

import lombok.Getter
import lombok.Setter
import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.samples.futgol.model.NamedEntity
import org.springframework.samples.futgol.partido.Partido
import java.util.*
import javax.persistence.*

@Getter
@Setter
@Entity
@Table(name = "equipoReal")
class EquipoReal : NamedEntity() {

    @Column(name = "posicion")
    var posicion = 0

    @Column(name = "partidos_jugados")
    var partidosJugados = 0

    @Column(name = "partidos_ganados")
    var partidosGanados = 0

    @Column(name = "partidos_empatados")
    var partidosEmpatados = 0

    @Column(name = "partidos_perdidos")
    var partidosPerdidos = 0

    @Column(name = "goles_a_favor")
    var golesAFavor = 0

    @Column(name = "goles_en_contra")
    var golesEnContra = 0

    @Column(name = "diferencia_goles")
    var diferenciaGoles = 0

    @Column(name = "puntos")
    var puntos = 0

    @Column(name = "escudo")
    var escudo = ""

    @Column(name = "formacion")
    var formacion = ""

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "club")
    var jugadores: MutableSet<Jugador> = HashSet()

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "equipoLocal")
    var partidosLocal: MutableSet<Partido> = HashSet()

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "equipoVisitante")
    var partidosVisitante: MutableSet<Partido> = HashSet()

}

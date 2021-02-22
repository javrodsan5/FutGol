package org.springframework.samples.futgol.equipoReal

import lombok.Getter
import lombok.Setter
import org.springframework.samples.futgol.equipo.Equipo
import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.samples.futgol.model.NamedEntity
import org.springframework.samples.futgol.usuario.Usuario
import java.util.HashSet
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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "club")
    var jugadores: MutableSet<Jugador> = HashSet()

}

package org.springframework.samples.futgol.equipoReal

import lombok.Getter
import lombok.Setter
import org.springframework.samples.futgol.model.NamedEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Getter
@Setter
@Entity
@Table(name = "equipoReal")
class equipoReal : NamedEntity() {

    @Column(name = "posicion")
    var posicion = 0

    @Column(name = "partidosJugados")
    var partidosJugados = 0

    @Column(name = "partidosGanados")
    var partidosGanados = 0

    @Column(name = "partidosEmpatados")
    var partidosEmpatados = 0

    @Column(name = "partidosPerdidos")
    var partidosPerdidos = 0

    @Column(name = "golesAFavor")
    var golesAFavor = 0

    @Column(name = "golesEnContra")
    var golesEnContra = 0

    @Column(name = "diferenciaGoles")
    var diferenciaGoles = 0

    @Column(name = "puntos")
    var puntos = 0

    @Column(name = "escudo")
    var escudo = null

}

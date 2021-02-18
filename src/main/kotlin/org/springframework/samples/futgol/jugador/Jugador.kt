package org.springframework.samples.futgol.jugador

import lombok.Getter
import lombok.Setter
import org.springframework.samples.futgol.equipo.Equipo
import org.springframework.samples.futgol.model.NamedEntity
import java.util.HashSet
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Getter
@Setter
@Entity
@Table(name = "jugadores")
class Jugador: NamedEntity() {

//    @Column(name = "pais")
//    @NotBlank
//    var pais = ""
//
//    @Column(name = "valor")
//    @NotBlank
//    var valor = ""
//
//    @Column(name = "foto")
//    @NotBlank
//    var foto = ""
//
//    @Column(name = "pie")
//    @NotBlank
//    var pie = ""
//
//    @Column(name = "posicion")
//    @NotBlank
//    var posicion = ""
//
//    @Column(name = "altura")
//    @NotBlank
//    var altura = ""
//
//    @Column(name = "peso")
//    @NotBlank
//    var peso = ""
//
//    @Column(name = "estadoLesion")
//    @NotBlank
//    var estadoLesion = ""
//
//    @ManyToOne()
//    @JoinColumn(name = "club", referencedColumnName = "id")
//    var club: Equipo? = null
//
//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(name = "equipo_jugadores", joinColumns = [JoinColumn(name = "equipo_id")], inverseJoinColumns = [JoinColumn(name = "jugador_id")])
//    var equipos: MutableSet<Equipo> = HashSet()
}

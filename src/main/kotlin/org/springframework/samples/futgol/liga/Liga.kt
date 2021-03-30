package org.springframework.samples.futgol.liga

import lombok.Getter
import lombok.Setter
import org.springframework.samples.futgol.equipo.Equipo
import org.springframework.samples.futgol.model.NamedEntity
import org.springframework.samples.futgol.usuario.Usuario
import java.util.*
import javax.persistence.*

@Getter
@Setter
@Entity
@Table(name = "ligas")
class Liga : NamedEntity() {

    @ManyToOne()
    @JoinColumn(name = "admin", referencedColumnName = "username")
    var admin: Usuario? = null

    @ManyToMany(mappedBy = "ligas")
    var usuarios: MutableSet<Usuario> = HashSet()

    @ManyToMany(mappedBy = "invitaciones")
    var usuariosInvitados: MutableSet<Usuario> = HashSet()

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "liga")
    var equipos: MutableSet<Equipo> = HashSet()

}

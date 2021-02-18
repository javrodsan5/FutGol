package org.springframework.samples.futgol.liga

import lombok.Getter
import lombok.Setter
import org.springframework.samples.futgol.equipo.Equipo
import org.springframework.samples.futgol.model.NamedEntity
import org.springframework.samples.futgol.usuario.Usuario
import java.util.HashSet
import javax.persistence.*
import javax.persistence.JoinColumn

@Getter
@Setter
@Entity
@Table(name = "ligas")
class Liga : NamedEntity() {

    @OneToMany()
    @JoinColumn(name = "equipos", referencedColumnName = "id")
    var equipos: MutableSet<Equipo>? = HashSet()

    @ManyToOne()
    @JoinColumn(name = "admin", referencedColumnName = "username")
    var admin: Usuario? = null

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuarios_ligas", joinColumns = [JoinColumn(name = "liga_id")], inverseJoinColumns = [JoinColumn(name = "usuario_id")])
    var usuarios: MutableSet<Usuario> = HashSet()

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "invitaciones", joinColumns = [JoinColumn(name = "liga_id")], inverseJoinColumns = [JoinColumn(name = "usuario_id")])
    var usuariosInvitados: MutableSet<Usuario> = HashSet()

}

package org.springframework.samples.futgol.liga

import lombok.Getter
import lombok.Setter
import org.springframework.samples.futgol.login.User
import org.springframework.samples.futgol.model.NamedEntity
import org.springframework.samples.futgol.usuario.Usuario
import java.util.HashSet
import javax.persistence.*


@Getter
@Setter
@Entity
@Table(name = "ligas")
class Liga : NamedEntity() {

    @ManyToOne()
    @JoinColumn(name = "admin", referencedColumnName = "username")
    var admin: Usuario? = null

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuarios_ligas", joinColumns = [JoinColumn(name = "usuario_id")], inverseJoinColumns = [JoinColumn(name = "liga_id")])
    var usuarios: MutableSet<Usuario> = HashSet()



}

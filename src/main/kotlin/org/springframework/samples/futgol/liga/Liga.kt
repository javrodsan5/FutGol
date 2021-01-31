package org.springframework.samples.futgol.liga

import lombok.Getter
import lombok.Setter
import org.springframework.samples.futgol.login.User
import org.springframework.samples.futgol.model.NamedEntity
import org.springframework.samples.futgol.usuario.Usuario
import javax.persistence.*


@Getter
@Setter
@Entity
@Table(name = "ligas")
class Liga : NamedEntity() {

    @ManyToOne()
    @JoinColumn(name = "username", referencedColumnName = "username")
    var admin: Usuario? = null



}

package org.springframework.samples.futgol.liga

import lombok.Getter
import lombok.Setter
import org.springframework.samples.futgol.login.Authorities
import org.springframework.samples.futgol.model.NamedEntity
import org.springframework.samples.futgol.usuario.Usuario
import javax.persistence.*


@Getter
@Setter
@Entity
@Table(name = "ligas")
class Liga : NamedEntity() {



}

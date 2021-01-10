package org.springframework.samples.futgol.usuario

import org.springframework.samples.futgol.model.NamedEntity
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "tipoUsuario")
class TipoUsuario : NamedEntity(){
}

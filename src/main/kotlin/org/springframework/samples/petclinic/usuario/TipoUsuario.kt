package org.springframework.samples.petclinic.usuario

import org.springframework.samples.petclinic.model.NamedEntity
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "tipoUsuario")
class TipoUsuario : NamedEntity(){
}

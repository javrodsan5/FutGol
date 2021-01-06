package org.springframework.samples.petclinic.usuario


import org.springframework.samples.petclinic.model.NamedEntity

import javax.persistence.*
import javax.validation.constraints.NotEmpty

@Entity
@Table(name = "usuarios")
class Usuario : NamedEntity() {

    @Column(name = "email")
    @NotEmpty
    var email = ""

    @Column(name = "nombreUsuario")
    @NotEmpty
    var nombreUsuario = ""


    @Column(name = "password")
    @NotEmpty
    var password = ""

}

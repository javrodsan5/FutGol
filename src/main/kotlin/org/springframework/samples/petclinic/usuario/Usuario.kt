package org.springframework.samples.petclinic.usuario


import org.springframework.samples.petclinic.model.NamedEntity

import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@Table(name = "usuarios")
class Usuario : NamedEntity() {

    @Column(name = "email")
    @NotBlank
    var email = ""

    @Column(name = "nombreUsuario")
    @NotBlank
    var nombreUsuario = ""

    @Column(name = "password")
    @NotBlank
    var password = ""

    //HAY QUE BUSCAR ALGUNA FORMA DE ENLAZAR SI ES ADMIN DE UNA LIGA
    @Column(name = "esAdmin")
    @NotNull
    var esAdmin = ""


}

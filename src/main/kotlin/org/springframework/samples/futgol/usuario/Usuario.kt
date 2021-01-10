package org.springframework.samples.futgol.usuario


import lombok.Getter
import lombok.Setter
import org.springframework.samples.futgol.liga.Liga
import org.springframework.samples.futgol.login.User
import org.springframework.samples.futgol.model.NamedEntity
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Getter
@Setter
@Entity
@Table(name = "usuarios")
class Usuario : NamedEntity() {

    @Column(name = "email")
    @NotBlank
    var email = ""

    //HAY QUE BUSCAR ALGUNA FORMA DE ENLAZAR SI ES ADMIN DE UNA LIGA
    @Column(name = "esAdmin")
    @NotNull
    var esAdmin = ""

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "username", referencedColumnName = "username")
    private val user: User? = null

    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "liga_id")
    private val ligas: Set<Liga>? = null


}

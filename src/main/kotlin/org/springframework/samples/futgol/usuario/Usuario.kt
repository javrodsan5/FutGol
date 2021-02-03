package org.springframework.samples.futgol.usuario


import lombok.Getter
import lombok.Setter
import org.springframework.samples.futgol.liga.Liga
import org.springframework.samples.futgol.login.Authorities
import org.springframework.samples.futgol.login.User
import org.springframework.samples.futgol.model.NamedEntity
import org.springframework.samples.futgol.owner.Pet
import org.springframework.samples.futgol.vet.Specialty
import java.util.HashSet
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Getter
@Setter
@Entity
@Table(name = "usuarios")
class Usuario : NamedEntity() {

    @Column(name = "email")
    @NotBlank
    @Email
    var email = ""

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "username", referencedColumnName = "username")
    var user: User? = null

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuarios_ligas", joinColumns = [JoinColumn(name = "usuario_id")], inverseJoinColumns = [JoinColumn(name = "liga_id")])
    var ligas: MutableSet<Liga> = HashSet()

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "invitaciones", joinColumns = [JoinColumn(name = "usuario_id")], inverseJoinColumns = [JoinColumn(name = "liga_id")])
    var invitaciones: MutableSet<Liga> = HashSet()


    fun addLiga(liga: Liga) {
        if (liga.isNew) {
            ligas.add(liga)
        }
    }

}

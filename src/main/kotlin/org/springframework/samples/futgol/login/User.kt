package org.springframework.samples.futgol.login

import lombok.Getter
import lombok.Setter
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Getter
@Setter
@Entity
@Table(name = "users")
public class User{

    @Id
    @NotBlank
    var username = ""

    @NotBlank
    var password = ""

    var enabled = false

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "user")
    private val authorities: Set<Authorities>? = null

}

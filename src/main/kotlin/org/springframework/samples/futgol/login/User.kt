package org.springframework.samples.futgol.login

import lombok.Getter
import lombok.Setter
import org.springframework.validation.annotation.Validated
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Getter
@Setter
@Entity
@Table(name = "users")
@Validated
public class User{

    @Id
    @NotBlank
    var username = ""

    @NotBlank
    @Size(min=5, max=30)
    var password = ""

    var enabled = false

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "user")
    private val authorities: Set<Authorities>? = null

}

package org.springframework.samples.futgol.login

import lombok.Getter
import lombok.Setter
import org.springframework.samples.futgol.model.BaseEntity
import org.springframework.validation.annotation.Validated
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Getter
@Setter
@Entity
@Table(name = "users")
@Validated
class User : BaseEntity() {

    @NotBlank
    var username = ""

    @NotBlank
    var password = ""

    var enabled = false

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "user")
    val authorities: MutableSet<Authorities>? = null

}

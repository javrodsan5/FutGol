package org.springframework.samples.futgol.login

import lombok.Getter
import lombok.Setter
import javax.persistence.*

@Getter
@Setter
@Entity
@Table(name = "users")
public class User{

    @Id
    var username = ""

    var password = ""

    var enabled = false

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "user")
    private val authorities: Set<Authorities>? = null

}

package org.springframework.samples.futgol.login

import lombok.Getter
import lombok.Setter
import org.springframework.samples.futgol.model.BaseEntity
import javax.persistence.*
import javax.validation.constraints.Size

@Getter
@Setter
@Entity
@Table(name = "authorities")
class Authorities : BaseEntity(){

    @ManyToOne
    @JoinColumn(name = "username")
    var user: User? = null

    @Size(min = 3, max = 50)
    var authority: String? = null
}

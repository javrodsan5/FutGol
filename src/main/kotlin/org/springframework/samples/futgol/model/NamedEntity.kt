package org.springframework.samples.futgol.model

import javax.persistence.Column
import javax.persistence.MappedSuperclass
import javax.validation.constraints.NotBlank

@MappedSuperclass
open class NamedEntity : BaseEntity() {

    @Column(name = "name")
    @NotBlank
    open var name: String? = null

    override fun toString(): String =
            this.name ?: ""

}

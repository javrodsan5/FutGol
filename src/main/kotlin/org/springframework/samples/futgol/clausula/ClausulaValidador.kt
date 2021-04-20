package org.springframework.samples.futgol.clausula

import org.springframework.validation.Errors
import org.springframework.validation.Validator

class ClausulaValidador : Validator {

    override fun validate(target: Any, errors: Errors) {
        val clausula = target as Clausula
        val valor = clausula.valorClausula
        when {
            valor == null -> errors.rejectValue(
                "valorClausula",
                "El campo no puede dejarse vacío",
                "El campo no puede dejarse vacío"
            )
            valor < 0 -> errors.rejectValue(
                "valorClausula",
                "El valor no puede ser negativo",
                "El valor no puede ser negativo"
            )
        }
    }

    override fun supports(clazz: Class<*>): Boolean {
        return Clausula::class.java.isAssignableFrom(clazz)
    }
}

package org.springframework.samples.futgol.liga

import org.springframework.util.StringUtils
import org.springframework.validation.Errors
import org.springframework.validation.Validator

class LigaValidador : Validator {

    private val REQUERIDO = "Campo requerido."

    override fun validate(target: Any, errors: Errors) {
        var liga = target as Liga
        var name = liga.name

        if (!StringUtils.hasLength(name)) {
            errors.rejectValue("name", REQUERIDO, REQUERIDO)
        }
    }

    override fun supports(clazz: Class<*>): Boolean {
        return Liga::class.java.isAssignableFrom(clazz)
    }


}

package org.springframework.samples.futgol.puja

import org.springframework.validation.Errors
import org.springframework.validation.Validator

class PujaValidador : Validator {

    override fun validate(target: Any, errors: Errors) {
        var puja = target as Puja
        var cantidad = puja.cantidad
        var equipo = puja.equipo!!

        if (cantidad == null) {
            errors.rejectValue("cantidad", "Campo requerido.", "Campo requerido.")
        }

        if (cantidad <= 0) {
            errors.rejectValue("cantidad", "El valor debe ser positivo", "El valor debe ser positivo")
        }

        if (cantidad > equipo.dineroRestante) {
            errors.rejectValue(
                "cantidad",
                "No puedes pujar por más dinero del que tienes",
                "No puedes pujar por más dinero del que tienes"
            )
        }
    }

    override fun supports(clazz: Class<*>): Boolean {
        return Puja::class.java.isAssignableFrom(clazz)
    }


}

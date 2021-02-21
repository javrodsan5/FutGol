package org.springframework.samples.futgol.usuario

import org.springframework.util.StringUtils
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import java.util.regex.Pattern

class UsuarioValidador : Validator {

    private val REQUERIDO = "Campo requerido."

    private val PATRON_EMAIL: Pattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    private fun soloLetrasNombre(nombre: String): Boolean {
        var res = false
        val sonSoloLetras: Boolean = Pattern.compile("[A-Za-zÁÉÍÓÚáéíóúñÑ ]").matcher(nombre).find()
        if (sonSoloLetras) {
            res = true
        }
        return res
    }

    private fun soloLetrasNumeros(nombreUsuario: String): Boolean {
        var res = false
        val sonSoloLetras: Boolean = Pattern.compile("[A-Za-z0-9]").matcher(nombreUsuario).find()
        if (sonSoloLetras) {
            res = true
        }
        return res
    }


    private fun checkPassword(password: String): Boolean {
        var tieneMayus = false
        var tieneMinus = false
        var tieneNumero = false
        for (ch in password) {
            when {
                Character.isDigit(ch) -> tieneNumero = true
                Character.isUpperCase(ch) -> tieneMayus = true
                Character.isLowerCase(ch) -> tieneMinus = true
            }
            if (tieneNumero && tieneMayus && tieneMinus) return true
        }
        return false
    }


    override fun validate(target: Any, errors: Errors) {
        var usuario = target as Usuario
        var name = usuario.name
        var email = usuario.email
        var username = usuario.user?.username
        var password = usuario.user?.password


        if (!StringUtils.hasLength(name)) {
            errors.rejectValue("name", REQUERIDO, REQUERIDO)
        }

        if (name?.let { soloLetrasNombre(it) } == false) {
            errors.rejectValue(
                "name",
                "El nombre solo puede contener letras",
                "El nombre solo puede contener letras"
            )
        }

        when {
            !StringUtils.hasLength(email) -> errors.rejectValue("email", REQUERIDO, REQUERIDO)
            !PATRON_EMAIL.matcher(email).matches() -> errors.rejectValue(
                "email",
                "Tu email debe tener un formato correcto",
                "Tu email debe tener un formato correcto"
            )

        }
        if (!StringUtils.hasLength(username)) {
            errors.rejectValue("user.username", REQUERIDO, REQUERIDO)
        }

        if (username?.let { soloLetrasNumeros(it) } == false) {
            errors.rejectValue(
                "user.username",
                "El nombre de usuario solo puede contener letras o números",
                "El nombre de usuario solo puede contener letras o números"
            )
        }

        if (password != null) {
            when {
                !StringUtils.hasLength(password) -> errors.rejectValue("user.password", REQUERIDO, REQUERIDO)
                password.length < 5 -> errors.rejectValue(
                    "user.password",
                    "La contraseña debe tener más de 5 caracteres",
                    "La contraseña debe tener más de 5 caracteres"
                )
                !checkPassword(password) -> errors.rejectValue(
                    "user.password",
                    "La contraseña debe tener al menos una mayúscula, un número y una minúscula",
                    "La contraseña debe tener al menos una mayúscula, un número y una minúscula"
                )
            }
        }

    }

    override fun supports(clazz: Class<*>): Boolean {
        return Usuario::class.java.isAssignableFrom(clazz)
    }
}

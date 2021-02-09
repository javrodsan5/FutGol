package org.springframework.samples.futgol.usuario

import org.springframework.util.StringUtils
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import java.util.regex.Pattern

class UsuarioValidador : Validator {

    private val REQUIRED = "Campo requerido."

    val EMAIL_ADDRESS_PATTERN: Pattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    override fun validate(target: Any, errors: Errors) {
        var usuario = target as Usuario
        var name = usuario.name
        var email = usuario.email
        var username = usuario.user?.username
        var password = usuario.user?.password


        if (!StringUtils.hasLength(name)) {
            errors.rejectValue("name", REQUIRED, REQUIRED);
        }

        when {
            !StringUtils.hasLength(email) -> errors.rejectValue("email", REQUIRED, REQUIRED);
            !EMAIL_ADDRESS_PATTERN.matcher(email).matches()-> errors.rejectValue(
                "email",
                "Tu email debe tener un formato correcto",
                "Tu email debe tener un formato correcto"
            );

        }
        if (!StringUtils.hasLength(username)) {
            errors.rejectValue("user.username", REQUIRED, REQUIRED);
        }

        if (password != null) {
            when {
                !StringUtils.hasLength(password) -> errors.rejectValue("user.password", REQUIRED, REQUIRED);
                password.length < 5 -> errors.rejectValue(
                    "user.password",
                    "La contraseña debe tener más de 5 caracteres",
                    "La contraseña debe tener más de 5 caracteres"
                );
            }
        }

    }

    override fun supports(clazz: Class<*>): Boolean {
        return Usuario::class.java.isAssignableFrom(clazz)
    }


}

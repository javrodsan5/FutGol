package org.springframework.samples.futgol.system

import org.springframework.samples.futgol.usuario.Usuario
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class LoginController {

    @RequestMapping("/login")
    fun login(): String = "/login"

    @RequestMapping("/loginError")
    fun loginError(): String = "login-error"
}

package org.springframework.samples.futgol.system

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class LoginController {

    @GetMapping("/login")
    fun login(): String {
        return "login"
    }

    @GetMapping("/loginError")
    fun loginError(model: Model): String {
        model.addAttribute("loginError", true)
        return "login"
    }
}

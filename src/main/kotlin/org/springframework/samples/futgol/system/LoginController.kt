package org.springframework.samples.futgol.system

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class LoginController {

    @RequestMapping("/login")
    fun login(): String = "/login"

    @RequestMapping("/loginError")
    fun loginError(model: Model): String{
        model.addAttribute("loginError",true)
        return "/login"
    }
}

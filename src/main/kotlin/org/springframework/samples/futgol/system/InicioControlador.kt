package org.springframework.samples.futgol.system

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class InicioControlador {

    @GetMapping("/")
    fun welcome(): String = "welcome"
}

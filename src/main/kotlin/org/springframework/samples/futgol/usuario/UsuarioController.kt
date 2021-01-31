package org.springframework.samples.futgol.usuario


import org.springframework.samples.futgol.login.User
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import java.security.Principal
import javax.validation.Valid

@Controller
class UsuarioController (val usuarioServicio: UsuarioServicio) {

    private val VISTA_REGISTRO_USUARIO = "usuarios/registroUsuario"
    private val VISTA_LISTADO_USUARIOS = "usuarios/usuariosList"
    private val VISTA_MISDATOS = "usuarios/misdatos"

    private val VISTA_LOGIN = "login/login"

    fun usuarioLogueado(principal: Principal): Usuario? {
        val username: String = principal.getName()
        return usuarioRepository.buscarUsuarioPorNombreUsuario(username)
    }

    @GetMapping("/micuenta")
    fun miCuenta(model: Model, principal: Principal): String {
        val usuario: Usuario? = usuarioLogueado(principal)
        if (usuario != null) {
            model["usuario"] = usuario
        }
        return VISTA_MISDATOS
    }


    @GetMapping("/usuarios")
    fun listarUsuarios(model: Model): String {
        val usuarios = usuarioServicio.findAllUsuarios()
        if (usuarios != null) {
            model["usuarios"]= usuarios
        }
        return VISTA_LISTADO_USUARIOS
    }

    @GetMapping("/usuarios/registro")
    fun iniciarCreacion(model: Model): String {
        val usuario = Usuario()
        model["usuario"] = usuario
        return VISTA_REGISTRO_USUARIO
    }

    @PostMapping("/usuarios/registro")
    fun processCreationForm(@Valid usuario: Usuario, result: BindingResult, model: Model): String {
        return if (result.hasErrors()) {
            model["usuario"] = usuario
            VISTA_REGISTRO_USUARIO
        } else {
            this.usuarioServicio.saveUsuario(usuario)
            "redirect:/"
        }
    }

    @RequestMapping("/login/login")
    fun login(): String{
        return VISTA_LOGIN
    }

}

package org.springframework.samples.futgol.usuario


import org.springframework.samples.futgol.login.AuthoritiesServicio
import org.springframework.samples.futgol.login.UserServicio
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.security.Principal
import javax.validation.Valid

@Controller
class UsuarioController (val usuarioServicio: UsuarioServicio, val userServicio: UserServicio, val authoritiesServicio: AuthoritiesServicio) {

    private val VISTA_REGISTRO_USUARIO = "usuarios/registroUsuario"
    private val VISTA_LISTADO_USUARIOS = "usuarios/usuariosList"
    private val VISTA_MISDATOS = "usuarios/misdatos"
    private val VISTA_EDITAR_USUARIO = "usuarios/editarUsuario"


    private val VISTA_LOGIN = "login/login"

    fun usuarioLogueado(principal: Principal): Usuario? {
        val username: String = principal.getName()
        return usuarioServicio.buscarUsuarioPorNombreUsuario(username)
    }

    @GetMapping("/micuenta")
    fun miCuenta(model: Model, principal: Principal): String {
        val usuario: Usuario? = usuarioLogueado(principal)
        if (usuario != null) {
            model["usuario"] = usuario
        }
        return VISTA_MISDATOS
    }



    @GetMapping("/usuarios/registro")
    fun iniciarCreacion(model: Model): String {
        val usuario = Usuario()
        model["usuario"] = usuario
        return VISTA_REGISTRO_USUARIO
    }

    @PostMapping("/usuarios/registro")
    fun procesoCreacion(@Valid usuario: Usuario, result: BindingResult, model: Model): String {
        return if (result.hasErrors()) {
            model["usuario"] = usuario
            VISTA_REGISTRO_USUARIO
        } else {
            //usuario.user?.let {this.userServicio?.saveUser(it)}
            this.usuarioServicio.saveUsuario(usuario)
            usuario.user?.username?.let { this.authoritiesServicio?.saveAuthorities(it, "usuario") }
            "redirect:/"
        }
    }

    @GetMapping("/micuenta/editarDatos/{idUsuario}")
    fun iniciarActualizacion(model: Model,  principal: Principal, @PathVariable("idUsuario") idUsuario: Int): String {
        val usuario = this.usuarioServicio.buscarUsuarioPorId(idUsuario)
        if (usuario != null) {
            model.addAttribute(usuario)
        }
        return VISTA_EDITAR_USUARIO
    }

    @PostMapping("/micuenta/editarDatos/{idUsuario}")
    fun procesoActualizacion(@Valid usuario: Usuario, principal: Principal, @PathVariable("idUsuario") idUsuario: Int, result: BindingResult, model: Model): String {
        return if (result.hasErrors()) {
            VISTA_EDITAR_USUARIO
        } else {
            usuario.id= idUsuario
            usuario.user = this.userServicio.findUser(principal.name)
            var usuarioUrl = this.usuarioServicio.findUsuarioById(idUsuario)
            if (usuarioUrl != null) {
                usuario.ligas = usuarioUrl.ligas
            }
            this.usuarioServicio.saveUsuario(usuario)

            "redirect:/micuenta"
        }
    }

    @RequestMapping("/login/login")
    fun login(): String{
        return VISTA_LOGIN
    }

}

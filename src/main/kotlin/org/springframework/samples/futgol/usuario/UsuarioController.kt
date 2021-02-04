package org.springframework.samples.futgol.usuario


import org.springframework.samples.futgol.liga.Liga
import org.springframework.samples.futgol.liga.LigaServicio
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
import java.util.HashSet
import javax.validation.Valid

@Controller
class UsuarioController (val usuarioServicio: UsuarioServicio, val userServicio: UserServicio, val authoritiesServicio: AuthoritiesServicio, val ligaServicio: LigaServicio) {

    private val VISTA_REGISTRO_USUARIO = "usuarios/registroUsuario"
    private val VISTA_LISTADO_USUARIOS = "usuarios/usuariosList"
    private val VISTA_MISDATOS = "usuarios/misdatos"
    private val VISTA_INVITACIONES = "usuarios/invitaciones"
    private val VISTA_EDITAR_USUARIO = "usuarios/editarUsuario"
    private val VISTA_DETALLES_USUARIO = "usuarios/detallesUsuario"



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

    @GetMapping("/micuenta/invitaciones")
    fun misInvitaciones(model: Model, principal: Principal): String {
        val usuario: Usuario? = usuarioLogueado(principal)
        var invitaciones = usuario?.invitaciones
        if (invitaciones != null && usuario!= null ) {
            model["invitaciones"] = invitaciones
            model["usuario"] = usuario
        }
        return VISTA_INVITACIONES
    }

    @GetMapping("/micuenta/invitaciones/{nombreLiga}/aceptar")
    fun aceptarInvitacion(model: Model, principal: Principal, @PathVariable("nombreLiga") nombreLiga: String): String {
        val usuario: Usuario? = usuarioLogueado(principal)
        var liga = this.ligaServicio.findLigaByName(nombreLiga)
        if (usuario != null && liga!= null) {
            usuario.ligas.add(liga)
            liga.usuarios.add(usuario)
            usuario.invitaciones.remove(liga)
            liga.usuariosInvitados.remove(usuario)
            this.usuarioServicio.saveUsuario(usuario)

        }
        return VISTA_INVITACIONES
    }

    @GetMapping("/micuenta/invitaciones/{nombreLiga}/rechazar")
    fun rechazarInvitacion(model: Model, principal: Principal, @PathVariable("nombreLiga") nombreLiga: String): String {
        val usuario: Usuario? = usuarioLogueado(principal)
        var liga = this.ligaServicio.findLigaByName(nombreLiga)
        if (usuario != null && liga!= null) {
            usuario.invitaciones.remove(liga)
            this.usuarioServicio.saveUsuario(usuario)
            liga.usuariosInvitados.remove(usuario)
            this.ligaServicio.saveLiga(liga)

        }
        return "redirect:/micuenta/invitaciones"
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
            this.usuarioServicio.saveUsuario(usuario)
            usuario.user?.username?.let { this.authoritiesServicio?.saveAuthorities(it, "usuario") }
            return "redirect:/"
        }
    }

    @GetMapping("/liga/{nombreLiga}/invitar/{nombreUsuario}")
    fun invitarUsuario(model: Model, @PathVariable("nombreLiga") nombreLiga: String, @PathVariable("nombreUsuario") nombreUsuario: String): String {
        var usuario = this.usuarioServicio.buscarUsuarioPorNombreUsuario(nombreUsuario)
        var liga = this.ligaServicio.findLigaByName(nombreLiga)
        if (usuario != null && liga!= null) {
            usuario.invitaciones.add(liga)
            liga.usuariosInvitados.add(usuario)
            this.usuarioServicio.saveUsuario(usuario)

        }
        return VISTA_INVITACIONES
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
                usuario.invitaciones = usuarioUrl.invitaciones
            }
            this.usuarioServicio.saveUsuario(usuario)

            "redirect:/micuenta"
        }
    }

    @GetMapping("usuarios/{username}")
    fun detallesUsuario(model: MutableMap<String, Any>, @PathVariable username: String, principal: Principal): String {

        var ligasNoUsuario: MutableList<Liga> = ArrayList()
        var usuario = usuarioServicio.buscarUsuarioPorNombreUsuario(username)
        var ligasUsuario = usuarioServicio.buscarLigasUsuario(username)

        var usuariologueado = usuarioLogueado(principal)
        var misLigas = usuariologueado?.user?.let { usuarioServicio.buscarLigasUsuario(it.username) }

        if (misLigas != null && ligasUsuario != null) {
            for(liga in misLigas) {
                if(liga !in ligasUsuario) {
                    ligasNoUsuario.add(liga)
                }
            }
        }
        if (usuario != null && ligasUsuario != null) {
            model["usuario"] = usuario
            model["ligasUsuario"] = ligasUsuario
            model["ligasNoUsuario"] = ligasNoUsuario
        }
        return VISTA_DETALLES_USUARIO
    }

}

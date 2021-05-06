package org.springframework.samples.futgol.usuario

import org.springframework.cache.annotation.CachePut
import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.liga.LigaServicio
import org.springframework.samples.futgol.login.AuthoritiesServicio
import org.springframework.samples.futgol.login.UserServicio
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import java.security.Principal
import java.util.regex.Pattern
import javax.validation.Valid


@Controller
class UsuarioControlador(
    val usuarioServicio: UsuarioServicio,
    val userServicio: UserServicio,
    val authoritiesServicio: AuthoritiesServicio,
    val ligaServicio: LigaServicio,
    val equipoServicio: EquipoServicio
) {

    private val VISTA_REGISTRO_USUARIO = "usuarios/registroUsuario"
    private val VISTA_MISDATOS = "usuarios/misdatos"
    private val VISTA_EDITAR_USUARIO = "usuarios/editarUsuario"
    private val VISTA_DETALLES_USUARIO = "usuarios/detallesUsuario"
    private val VISTA_BUSCAR_USUARIO = "usuarios/buscarUsuario"
    private val VISTA_RANKING_USUARIOS = "usuarios/rankingUsuarios"

    val PATRON_EMAIL: Pattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )
    val PATRON_NOMBRE: Pattern = Pattern.compile("^[\\p{L} .'-]+$")

    @InitBinder("usuario")
    fun initUsuarioBinder(dataBinder: WebDataBinder) {
        dataBinder.validator = UsuarioValidador()
    }

    @GetMapping("/micuenta")
    fun miCuenta(model: Model, principal: Principal): String {
        model["usuario"] = usuarioServicio.usuarioLogueado(principal)!!
        return VISTA_MISDATOS
    }


    @GetMapping("/micuenta/invitaciones/{nombreLiga}/aceptar")
    fun aceptarInvitacion(model: Model, principal: Principal, @PathVariable("nombreLiga") nombreLiga: String): String {
        if (ligaServicio.comprobarSiExisteLiga(nombreLiga) == true) {
            val usuario: Usuario? = usuarioServicio.usuarioLogueado(principal)
            var liga = this.ligaServicio.buscarLigaPorNombre(nombreLiga)!!
            if (usuario != null && liga in usuario.invitaciones) {
                usuario.ligas.add(liga)
                usuario.invitaciones.removeIf { it.name == liga.name }
                liga.usuariosInvitados.removeIf { it.user?.username == usuario.user?.username }
                this.usuarioServicio.guardarUsuario(usuario)
                this.ligaServicio.guardarLiga(liga)
            }
            return "redirect:/micuenta"
        }
        return "redirect:/"
    }

    @GetMapping("/micuenta/invitaciones/{nombreLiga}/rechazar")
    fun rechazarInvitacion(model: Model, principal: Principal, @PathVariable("nombreLiga") nombreLiga: String): String {
        if (ligaServicio.comprobarSiExisteLiga(nombreLiga) == true) {
            val usuario: Usuario? = usuarioServicio.usuarioLogueado(principal)
            var liga = this.ligaServicio.buscarLigaPorNombre(nombreLiga)
            if (usuario != null && liga != null) {
                usuario.invitaciones.removeIf { it.name == liga.name }
                liga.usuariosInvitados.removeIf { it.user?.username == usuario.user?.username }
                this.usuarioServicio.guardarUsuario(usuario)
                this.ligaServicio.guardarLiga(liga)
            }
            return "redirect:/micuenta"
        }
        return "redirect:/"
    }

    @GetMapping("/usuarios/registro")
    fun iniciarCreacion(model: Model): String {
        model["usuario"] = Usuario()
        return VISTA_REGISTRO_USUARIO
    }

    @PostMapping("/usuarios/registro")
    fun procesoCreacion(@Valid usuario: Usuario, result: BindingResult, model: Model): String {
        when {
            usuarioServicio.comprobarSiNombreUsuarioExiste(usuario.user?.username) == true ->
                result.addError(FieldError("usuario", "user.username", "El nombre de usuario ya está en uso"))
            usuarioServicio.comprobarSiEmailExiste(usuario.email) == true ->
                result.addError(FieldError("usuario", "email", "El email ya está en uso"))
        }
        return if (result.hasErrors()) {
            model["usuario"] = usuario
            VISTA_REGISTRO_USUARIO
        } else {
            this.usuarioServicio.guardarUsuario(usuario)
            usuario.user?.let { this.userServicio.guardarUser(it, true) }
            usuario.user?.username?.let { this.authoritiesServicio.guardarAutoridades(it, "usuario") }
            return "login"
        }
    }

    @GetMapping("/micuenta/editarmisdatos")
    fun iniciarActualizacion(model: Model, principal: Principal): String {
        model["usuario"] = usuarioServicio.usuarioLogueado(principal)!!
        return VISTA_EDITAR_USUARIO
    }

    @PostMapping("/micuenta/editarmisdatos")
    fun procesoActualizacion(
        @Valid usuario: Usuario,
        result: BindingResult,
        principal: Principal,
        model: Model
    ): String {
        var usuarioComparador = usuarioServicio.usuarioLogueado(principal)

        return if (result.hasErrors()) {
            model["usuario"] = usuario
            VISTA_EDITAR_USUARIO
        } else {
            if (usuarioComparador != null) {

                var user = userServicio.buscarUser(usuarioComparador.user?.username)
                user?.username = usuario.user?.username!!
                var b = false
                if (user != null) {
                    if (!BCryptPasswordEncoder().matches(usuario.user!!.password, user.password)) {
                        b = true
                        user.password = usuario.user?.password!!
                    }
                    this.userServicio.guardarUser(user, b)
                }

                usuarioComparador.name = usuario.name
                usuarioComparador.email = usuario.email
                this.usuarioServicio.guardarUsuario(usuarioComparador)

            }
            "redirect:/micuenta"
        }
    }

    @GetMapping("/liga/{nombreLiga}/invitar/{nombreUsuario}")
    fun invitarUsuario(
        model: Model,
        @PathVariable("nombreLiga") nombreLiga: String,
        @PathVariable("nombreUsuario") nombreUsuario: String
    ): String {
        if (usuarioServicio.comprobarSiNombreUsuarioExiste(nombreUsuario) == true && ligaServicio.comprobarSiExisteLiga(
                nombreLiga
            ) == true
        ) {
            var usuario = this.usuarioServicio.buscarUsuarioPorNombreUsuario(nombreUsuario)!!
            var liga = this.ligaServicio.buscarLigaPorNombre(nombreLiga)!!
            if (liga.equipos.size >= 8 || liga in usuario.ligas || liga in usuario.invitaciones) {
                return "redirect:/misligas"
            }
            usuario.invitaciones.add(liga)
            liga.usuariosInvitados.add(usuario)
            this.usuarioServicio.guardarUsuario(usuario)
            return "redirect:/usuarios/$nombreUsuario"
        }
        return "redirect:/misligas"
    }

    @CachePut("detallesUsuario")
    @GetMapping("usuarios/{username}")
    fun detallesUsuario(model: MutableMap<String, Any>, @PathVariable username: String, principal: Principal): String {
        if (usuarioServicio.comprobarSiNombreUsuarioExiste(username) == true) {
            var usuario = usuarioServicio.buscarUsuarioPorNombreUsuario(username)!!
            if (usuario.user?.username == usuarioServicio.usuarioLogueado(principal)!!.user?.username) {
                return "redirect:/micuenta"
            }
            model["usuario"] = usuario
            model["ligasUsuario"] = usuarioServicio.buscarLigasUsuario(username)!!
            model["ligasNoUsuario"] = usuarioServicio.ligasAInvitar(username, principal)
            return VISTA_DETALLES_USUARIO
        }
        return "redirect:/"
    }

    @GetMapping("/usuarios/buscar")
    fun iniciarBusquedaUsuarioLiga(model: Model, principal: Principal): String {
        var usuario = Usuario()
        var usuarios = usuarioServicio.buscarTodosNombresUsuarios()
        usuarios?.removeIf { it == principal.name }

        model.addAttribute(usuario)
        if (usuarios != null) {
            model["usuarios"] = usuarios
        }
        return VISTA_BUSCAR_USUARIO
    }

    @GetMapping("/usuarios")
    fun procesoBusquedaUsuarioLiga(usuario: Usuario, result: BindingResult, model: Model): String {
        return if (usuarioServicio.comprobarSiNombreUsuarioExiste(usuario.user?.username) == true) {
            "redirect:/usuarios/" + usuario.user?.username
        } else {
            result.rejectValue("user.username", "no se ha encontrado", "no se ha encontrado")
            "redirect:/misligas"
        }
    }

    @GetMapping("topUsuarios")
    fun clasificacionGeneral(model: Model): String {
        var equipos = equipoServicio.buscaTodosEquiposOrdenPuntos()?.toList()?.subList(0,9)
        if (equipos != null) {
            model["ganador"] = equipos[0]
            model["segundo"] = equipos[1]
            model["tercero"] = equipos[2]
            model["restoEquipos"] = equipos.drop(7)
        }
        return VISTA_RANKING_USUARIOS
    }
}

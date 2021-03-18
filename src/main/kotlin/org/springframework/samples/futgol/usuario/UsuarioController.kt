package org.springframework.samples.futgol.usuario

import org.springframework.cache.annotation.CachePut
import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.liga.LigaServicio
import org.springframework.samples.futgol.login.AuthoritiesServicio
import org.springframework.samples.futgol.login.UserServicio
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.util.StringUtils
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
class UsuarioController(
    val usuarioServicio: UsuarioServicio,
    val userServicio: UserServicio,
    val authoritiesServicio: AuthoritiesServicio,
    val ligaServicio: LigaServicio,
    val equipoServicio: EquipoServicio
) {

    private val VISTA_REGISTRO_USUARIO = "usuarios/registroUsuario"
    private val VISTA_MISDATOS = "usuarios/misdatos"
    private val VISTA_INVITACIONES = "usuarios/invitaciones"
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

    @GetMapping("/micuenta/invitaciones")
    fun misInvitaciones(model: Model, principal: Principal): String {
        val usuario = usuarioServicio.usuarioLogueado(principal)
        model["invitaciones"] = usuario?.invitaciones!!
        model["usuario"] = usuario
        return VISTA_INVITACIONES
    }

    @GetMapping("/micuenta/invitaciones/{nombreLiga}/aceptar")
    fun aceptarInvitacion(model: Model, principal: Principal, @PathVariable("nombreLiga") nombreLiga: String): String {
        val usuario: Usuario? = usuarioServicio.usuarioLogueado(principal)
        var liga = this.ligaServicio.buscarLigaPorNombre(nombreLiga)
        if (usuario != null && liga != null) {
            usuario.ligas.add(liga)
            usuario.invitaciones.removeIf { it.name == liga.name }
            liga.usuariosInvitados.removeIf { it.user?.username == usuario.user?.username }
            this.usuarioServicio.guardarUsuario(usuario)
            this.ligaServicio.guardarLiga(liga)
        }
        return "redirect:/micuenta/invitaciones"
    }

    @GetMapping("/micuenta/invitaciones/{nombreLiga}/rechazar")
    fun rechazarInvitacion(model: Model, principal: Principal, @PathVariable("nombreLiga") nombreLiga: String): String {
        val usuario: Usuario? = usuarioServicio.usuarioLogueado(principal)
        var liga = this.ligaServicio.buscarLigaPorNombre(nombreLiga)
        if (usuario != null && liga != null) {
            usuario.invitaciones.removeIf { it.name == liga.name }
            liga.usuariosInvitados.removeIf { it.user?.username == usuario.user?.username }
            this.usuarioServicio.guardarUsuario(usuario)
            this.ligaServicio.guardarLiga(liga)
        }
        return "redirect:/micuenta/invitaciones"
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
            usuario.user?.username?.let { this.authoritiesServicio.saveAuthorities(it, "usuario") }
            return "redirect:/"
        }
    }

    @GetMapping("/micuenta/editarmisdatos")
    fun iniciarActualizacion(model: Model, principal: Principal): String {
        model["usuario"] = usuarioServicio.usuarioLogueado(principal)!!
        return VISTA_EDITAR_USUARIO
    }

    @PostMapping("/micuenta/editarmisdatos")
    fun procesoActualizacion(usuario: Usuario, result: BindingResult, principal: Principal, model: Model): String {
        var usuarioComparador = usuarioServicio.usuarioLogueado(principal)
        if (usuarioComparador != null) {
            when {
                usuario.email != usuarioComparador.email && usuarioServicio.comprobarSiEmailExiste(usuario.email) == true ->
                    result.addError(FieldError("usuario", "email", "El email ya está en uso"))
                !StringUtils.hasLength(usuario.email) ->
                    result.addError(FieldError("usuario", "email", "El email no puedes dejarlo vacío"))
                !PATRON_EMAIL.matcher(usuario.email).matches() ->
                    result.addError(FieldError("usuario", "email", "Tu email debe tener un formato correcto"))
                !StringUtils.hasLength(usuario.name) ->
                    result.addError(FieldError("usuario", "name", "El nombre no puedes dejarlo vacío"))
                !PATRON_NOMBRE.matcher(usuario.name).matches() ->
                    result.addError(FieldError("usuario", "name", "Tu nombre solo puede tener letras"))
            }
        }
        return if (result.hasErrors()) {
            model["usuario"] = usuario
            VISTA_EDITAR_USUARIO
        } else {
            if (usuarioComparador != null) {
                usuario.id = usuarioComparador.id
                usuario.ligas = usuarioComparador.ligas
                usuario.invitaciones = usuarioComparador.invitaciones
                usuario.user = this.userServicio.findUser(principal.name)
                usuario.user?.username = principal.name

            }
            this.usuarioServicio.guardarUsuario(usuario)
            "redirect:/micuenta"
        }
    }

    @GetMapping("/liga/{nombreLiga}/invitar/{nombreUsuario}")
    fun invitarUsuario(
        model: Model,
        @PathVariable("nombreLiga") nombreLiga: String,
        @PathVariable("nombreUsuario") nombreUsuario: String
    ): String {
        var usuario = this.usuarioServicio.buscarUsuarioPorNombreUsuario(nombreUsuario)
        var liga = this.ligaServicio.buscarLigaPorNombre(nombreLiga)
        if (liga!!.equipos.size >= 8) {
            "redirect:/misligas"
        }
        if (usuario != null && liga != null) {
            usuario.invitaciones.add(liga)
            liga.usuariosInvitados.add(usuario)
            this.usuarioServicio.guardarUsuario(usuario)
        }
        return "redirect:/misligas"
    }

    @CachePut("detallesUsuario")
    @GetMapping("usuarios/{username}")
    fun detallesUsuario(model: MutableMap<String, Any>, @PathVariable username: String, principal: Principal): String {
        var usuario = usuarioServicio.buscarUsuarioPorNombreUsuario(username)!!
        if (usuario.user?.username == usuarioServicio.usuarioLogueado(principal)!!.user?.username) {
            return "redirect:/micuenta"
        }
        model["usuario"] = usuario
        model["ligasUsuario"] = usuarioServicio.buscarLigasUsuario(username)!!
        model["ligasNoUsuario"] = usuarioServicio.ligasAInvitar(username, principal)
        return VISTA_DETALLES_USUARIO
    }

    @GetMapping("/usuarios/buscar")
    fun iniciarBusquedaUsuarioLiga(model: Model, principal: Principal): String {
        var usuario = Usuario()
        var usuarios = usuarioServicio.buscarTodosUsuarios()
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
            "redirect:/usuarios/buscar"
        }
    }

    @GetMapping("topUsuarios")
    fun clasificacionGeneral(model: Model): String {
        var equipos = equipoServicio.buscaTodosEquipos()?.sortedBy { x -> -x.puntos }?.subList(0, 4)
        if (equipos != null) {
            model["equipos"] = equipos
//            var valores: MutableList<Double> = ArrayList()
//            for (e in equipos) {
//                e.liga?.id?.let { equipoServicio.calcularValorEquipo(e.name, it) }?.let { valores.add(it) }
//            }
//            model["valores"] = valores
//            model["posiciones"] = equipos.indices
        }
        return VISTA_RANKING_USUARIOS
    }
}

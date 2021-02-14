package org.springframework.samples.futgol.liga


import org.springframework.samples.futgol.usuario.Usuario
import org.springframework.samples.futgol.usuario.UsuarioServicio
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
import javax.validation.Valid


@Controller
class LigaControlador(val ligaServicio: LigaServicio, val usuarioServicio: UsuarioServicio) {

    private val VISTA_CREAR_EDITAR_LIGA = "liga/crearEditarLiga"
    private val VISTA_LISTA_LIGAS = "liga/listaLigas"
    private val VISTA_DETALLES_LIGA = "liga/detallesLiga"
    private val VISTA_ERROR_403 = "errores/error-403"


    @InitBinder("liga")
    fun initLigaBinder(dataBinder: WebDataBinder) {
        dataBinder.validator = LigaValidador()
    }

    fun usuarioLogueado(principal: Principal): Usuario? {
        val username: String = principal.name
        return usuarioServicio.buscarUsuarioPorNombreUsuario(username)
    }

    @GetMapping("/misligas")
    fun listaLigas(model: Model, principal: Principal): String {
        val usuario: Usuario? = usuarioLogueado(principal)
        val ligas = usuario?.user?.username?.let { usuarioServicio.buscarLigasUsuario(it) }
        if (ligas != null) {
            model["ligas"] = ligas
        }
        return VISTA_LISTA_LIGAS
    }

    @GetMapping("/liga/crear")
    fun iniciarCreacion(model: Model, principal: Principal): String {
        val liga = Liga()
        model["liga"] = liga
        return VISTA_CREAR_EDITAR_LIGA
    }

    @PostMapping("/liga/crear")
    fun procesoCrear(@Valid liga: Liga, result: BindingResult, principal: Principal, model: Model): String {

        if (ligaServicio.checkLigaExists(liga.name)) {
            result.addError(FieldError("liga", "name", "Ya existe una liga con ese nombre"))
        }
        return if (result.hasErrors()) {
            model["liga"] = liga
            VISTA_CREAR_EDITAR_LIGA
        } else {
            val usuario: Usuario? = usuarioLogueado(principal)
            liga.admin = usuario
            usuario?.ligas?.add(liga)
            if (usuario != null) {
                liga.usuarios.add(usuario)
                this.ligaServicio.saveLiga(liga)

            }
            "redirect:/misligas"
        }
    }

    @GetMapping("/liga/editar/{idLiga}")
    fun initUpdateForm(@PathVariable idLiga: Int, model: Model, principal: Principal): String {
        val liga = this.ligaServicio.buscarLigaPorId(idLiga)
        var adminLiga = liga?.admin?.user?.username
        val usuario = usuarioLogueado(principal)?.user?.username
        if (liga != null && usuario != null && adminLiga != usuario) {
            return VISTA_ERROR_403
        }
        val ligas = usuario?.let { usuarioServicio.buscarLigasUsuario(it) }
        if (ligas != null && liga != null && ligas.stream().anyMatch { x -> x.id?.equals(idLiga) == true }) {
            model.addAttribute(liga)
        } else {
            return VISTA_ERROR_403
        }

        return VISTA_CREAR_EDITAR_LIGA
    }

    @PostMapping("/liga/editar/{idLiga}")
    fun procesoActualizacion(
        liga: Liga, principal: Principal, @PathVariable("idLiga") idLiga: Int, result: BindingResult,
        model: Model
    ): String {
        var ligaComparador = ligaServicio.buscarLigaPorId(idLiga)
        if (ligaComparador != null) {
            if (liga.name != ligaComparador.name && ligaServicio.checkLigaExists(liga.name)) {
                result.addError(FieldError("usuario", "name", "Ya existe una liga con ese nombre"))
            }
        }
        return if (result.hasErrors()) {
            VISTA_CREAR_EDITAR_LIGA
        } else {

            val ligaAntigua = this.ligaServicio.buscarLigaPorId(idLiga)
            if (ligaAntigua != null) {
                liga.id = idLiga
                liga.admin = ligaAntigua?.admin
                liga.usuariosInvitados = ligaAntigua.usuariosInvitados
                liga.usuarios = ligaAntigua.usuarios
            }

            this.ligaServicio.saveLiga(liga)
            "redirect:/misligas"
        }
    }

    @GetMapping("liga/{nombreLiga}")
    fun detallesLiga(model: MutableMap<String, Any>, @PathVariable nombreLiga: String, principal: Principal): String {
        val liga = ligaServicio.findLigaByName(nombreLiga)
        var soyAdmin = false
        val usuario = usuarioLogueado(principal)
        val ligas = usuario?.user?.username?.let { usuarioServicio.buscarLigasUsuario(it) }
        if (ligas != null) {
            if (liga != null && ligas.stream().anyMatch { x -> x.name.equals(nombreLiga) }) {
                model["liga"] = liga
            } else {
                return VISTA_ERROR_403
            }
        }
        if (liga != null && usuario != null) {
            if (liga.admin?.user?.username.equals(usuario.user?.username)) {
                soyAdmin = true
                model["soyAdmin"] = soyAdmin
            }
        }
        return VISTA_DETALLES_LIGA
    }

    @GetMapping("/usuarios/buscar")
    fun iniciarBusquedaUsuarioLiga(model: MutableMap<String, Any>): String {
        var usuario = Usuario()
        var usuarios = usuarioServicio.buscarTodosUsuarios()

        if(usuario!=null) {
            model["usuario"] = usuario
        }
        if(usuarios!=null){
            model["usuarios"] = usuarios
        }
        return "usuarios/buscarUsuario"
    }

    @GetMapping("/usuarios")
    fun procesoBusquedaUsuarioLiga(@Valid usuario: Usuario, result: BindingResult, model: MutableMap<String, Any>): String {
        var res = VISTA_DETALLES_LIGA
        val usuario = usuario.user?.let { usuarioServicio.buscarUsuarioPorNombreUsuario(it.username) }

        if (usuario != null) res = "redirect:/usuarios/" + (usuario.user?.username)
        return res
    }

    @GetMapping("/liga/{nombreLiga}/add/{username}")
    fun asociarUsuarioLiga(
        @PathVariable username: String,
        @PathVariable nombreLiga: String,
        model: MutableMap<String, Any>
    ): String {

        var usuario = usuarioServicio.buscarUsuarioPorNombreUsuario(username)
        var liga = ligaServicio.findLigaByName(nombreLiga)
        if (usuario != null && liga != null) {
            if (!usuario.ligas.contains(liga)) {
                usuario.ligas.add(liga)
                liga.usuarios.add(usuario)
                model["mensaje"] = "Usuario añadido correctamente a la liga!"
            } else {
                model["mensaje"] = "El usuario ya pertenece a la liga"
            }
        }

        return VISTA_DETALLES_LIGA
    }
}

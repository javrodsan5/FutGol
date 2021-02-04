package org.springframework.samples.futgol.liga


import org.springframework.samples.futgol.usuario.Usuario
import org.springframework.samples.futgol.usuario.UsuarioServicio
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import java.security.Principal
import javax.validation.Valid


@Controller
class LigaControlador(val ligaServicio: LigaServicio, val usuarioServicio: UsuarioServicio) {

    private val VISTA_CREAR_EDITAR_LIGA = "liga/crearEditarLiga"
    private val VISTA_LISTA_LIGAS = "liga/listaLigas"
    private val VISTA_DETALLES_LIGA = "liga/detallesLiga"

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
    fun iniciarCreacion(model: MutableMap<String, Any>, principal: Principal): String {
        val liga = Liga()
        model["liga"] = liga
        return VISTA_CREAR_EDITAR_LIGA
    }

    @PostMapping("/liga/crear")
    fun procesoCrear(@Valid liga: Liga, principal: Principal, result: BindingResult): String {
        return if (result.hasErrors()) {
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
    fun initUpdateForm(@PathVariable idLiga: Int, model: Model): String {
        val liga = this.ligaServicio.buscarLigaPorId(idLiga)
        if (liga != null) {
            model.addAttribute(liga)
        }
        return VISTA_CREAR_EDITAR_LIGA
    }

    @PostMapping("/liga/editar/{idLiga}")
    fun procesoActualizacion(
        @Valid liga: Liga,
        principal: Principal,
        @PathVariable("idLiga") idLiga: Int,
        result: BindingResult,
        model: Model
    ): String {
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
    fun detallesLiga(model: MutableMap<String, Any>, @PathVariable nombreLiga: String): String {
        val liga = ligaServicio.findLigaByName(nombreLiga)
        if (liga != null) {
            model["liga"] = liga
        }
        return VISTA_DETALLES_LIGA
    }



    @GetMapping("/usuarios/buscar")
    fun initFindForm(model: MutableMap<String, Any>): String {
        var usuario = Usuario()
        model["usuario"] = usuario
        return "usuarios/buscarUsuario"
    }

    @GetMapping("/usuarios")
    fun buscarUsuarioparaLiga(usuario: Usuario, model: MutableMap<String, Any>): String {
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

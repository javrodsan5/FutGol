package org.springframework.samples.futgol.liga


import org.springframework.samples.futgol.owner.Owner
import org.springframework.samples.futgol.usuario.Usuario
import org.springframework.samples.futgol.usuario.UsuarioRepository
import org.springframework.samples.futgol.usuario.UsuarioServicio
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.stereotype.Controller
import java.security.Principal
import javax.validation.Valid


@Controller
class LigaControlador (val ligaServicio: LigaServicio, val usuarioServicio: UsuarioServicio) {

    private val VISTA_CREAR_LIGA = "liga/crearLiga"
    private val VISTA_LISTA_LIGAS = "liga/listaLigas"

    fun usuarioLogueado(principal: Principal): Usuario? {
        val username: String = principal.getName()
        return usuarioServicio.buscarUsuarioPorNombreUsuario(username)
    }

    @GetMapping("/misligas")
    fun listaLigas(model: Model, principal: Principal): String {
        val usuario: Usuario? = usuarioLogueado(principal)
        if (usuario != null) {
            model["usuario"] = usuario
        }
        val ligas = usuario?.user?.username?.let { usuarioServicio.buscarLigasUsuario(it) }
        if (ligas != null) {
            model["ligas"]= ligas
        }
        return VISTA_LISTA_LIGAS
    }

    @GetMapping("/liga/crear")
    fun iniciarCreacion(model: MutableMap<String, Any>, principal: Principal): String {
        val liga = Liga()
        model["liga"] = liga
        return VISTA_CREAR_LIGA
    }

    @PostMapping("/liga/crear")
    fun procesoCrear(@Valid liga: Liga, principal: Principal, result: BindingResult): String {
        return if (result.hasErrors()) {
            VISTA_CREAR_LIGA
        } else {
            val  usuario: Usuario? = usuarioLogueado(principal)
            liga.admin= usuario
            usuario?.ligas?.add(liga)
            this.ligaServicio.saveLiga(liga)
            if (usuario != null) {
                this.usuarioServicio.saveUsuario(usuario)
            }
            "redirect:/misligas"
        }
    }

//    @GetMapping("/owners/new")
//    fun initCreationForm(model: MutableMap<String, Any>): String {
//        val owner = Owner()
//        model["owner"] = owner
//        return VIEWS_OWNER_CREATE_OR_UPDATE_FORM
//    }
//
//    @PostMapping("/owners/new")
//    fun processCreationForm(@Valid owner: Owner, result: BindingResult): String {
//        return if (result.hasErrors()) {
//            VIEWS_OWNER_CREATE_OR_UPDATE_FORM
//        } else {
//            owners.save(owner)
//            "redirect:/owners/" + owner.id
//        }
//    }
}

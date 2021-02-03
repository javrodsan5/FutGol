package org.springframework.samples.futgol.liga


import org.springframework.samples.futgol.owner.Owner
import org.springframework.samples.futgol.owner.Pet
import org.springframework.samples.futgol.owner.PetValidator
import org.springframework.samples.futgol.usuario.Usuario
import org.springframework.samples.futgol.usuario.UsuarioServicio
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
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
    fun procesoCrear(@Valid liga: Liga, principal: Principal, result: BindingResult, model: Model): String {
        return if (result.hasErrors()) {
            model["liga"] = liga
            VISTA_CREAR_EDITAR_LIGA
        } else {
            val usuario: Usuario? = usuarioLogueado(principal)
            liga.admin = usuario
            usuario?.ligas?.add(liga)
            this.ligaServicio.saveLiga(liga)
            if (usuario != null) {
                this.usuarioServicio.saveUsuario(usuario)
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
    fun procesoActualizacion(@Valid liga: Liga, principal: Principal, @PathVariable("idLiga") idLiga: Int, result: BindingResult, model: Model): String {
        return if (result.hasErrors()) {
            VISTA_CREAR_EDITAR_LIGA
        } else {
            val ligaAntigua = this.ligaServicio.buscarLigaPorId(idLiga)
            liga.id= idLiga
            liga.admin= ligaAntigua?.admin
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

}

package org.springframework.samples.futgol.liga


import org.springframework.samples.futgol.usuario.Usuario
import org.springframework.samples.futgol.usuario.UsuarioRepository
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.stereotype.Controller
import java.security.Principal
import javax.validation.Valid


@Controller
class LigaControlador (val ligaRepositorio: LigaRepositorio, val usuarioRepository: UsuarioRepository) {

    private val VISTA_CREAR_LIGA = "liga/crearLiga"
    private val VISTA_LISTA_LIGAS = "liga/listaLigas"

    fun usuarioLogueado(principal: Principal): Usuario? {
        val username: String = principal.getName()
        return usuarioRepository.buscarUsuarioPorNombreUsuario(username)
    }

    @GetMapping("/misligas")
    fun listaLigas(model: Model, principal: Principal): String {
        val usuario: Usuario? = usuarioLogueado(principal)
        if (usuario != null) {
            model["usuario"] = usuario
        }
        val ligas = usuario?.autoridad?.user?.username?.let { usuarioRepository.buscarLigasUsuario(it) }
        if (ligas != null) {
            model["ligas"]= ligas
        }
        return VISTA_LISTA_LIGAS
    }

    @GetMapping("/liga/crear")
    fun iniciarCreacion(model: Model, usuario: Usuario): String {
        val liga = Liga()
        usuario.addLiga(liga)
        model["liga"] = liga
        return VISTA_CREAR_LIGA
    }

    @PostMapping("/liga/crear")
    fun procesoCrear(@Valid liga: Liga, usuario: Usuario, result: BindingResult, model: Model): String {
        usuario.addLiga(liga)
        return if (result.hasErrors()) {
            model["liga"] = liga
            VISTA_CREAR_LIGA
        } else {
            this.ligaRepositorio.save(liga)
            "redirect:/"
        }
    }
}

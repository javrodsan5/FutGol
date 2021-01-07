package org.springframework.samples.petclinic.usuario


import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.stereotype.Controller
import javax.validation.Valid

@Controller
class UsuarioController (val usuarioRepository: UsuarioRepository) {

    private val VISTA_REGISTRO_USUARIO = "usuarios/registroUsuario"
    private val VISTA_LISTADO_USUARIOS = "usuarios/usuariosList"


    @GetMapping("/usuarios")
    fun listarUsuarios(model: MutableMap<String, Any>): String {
        val usuarios = usuarioRepository.findAll()
        model["usuarios"]= usuarios
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
            this.usuarioRepository.save(usuario)
            "redirect:/"
        }
    }
}
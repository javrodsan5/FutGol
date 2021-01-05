package org.springframework.samples.petclinic.usuario

import org.springframework.web.bind.annotation.GetMapping

class UsuarioController (val usuarioRepository: UsuarioRepository) {

    @GetMapping("/usuarios")
    fun listUsuarios(model: MutableMap<String, Any>): String {
        val usuarios = usuarioRepository.findAll()
        model["usuarios"]= usuarios
        return "usuarios/usuariosList"
    }
}

package org.springframework.samples.futgol.usuario

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.liga.Liga
import org.springframework.samples.futgol.login.UserServicio
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.Principal
import java.util.stream.Collectors

@Service
class UsuarioServicio {

    private var usuarioRepository: UsuarioRepository? = null

    @Autowired
    private val userService: UserServicio? = null

    @Autowired
    fun UsuarioServicio(usuarioRepository: UsuarioRepository?) {
        this.usuarioRepository = usuarioRepository
    }

    fun usuarioLogueado(principal: Principal): Usuario? {
        val username: String = principal.name
        return buscarUsuarioPorNombreUsuario(username)
    }

    @Transactional
    @Throws(DataAccessException::class)
    fun guardarUsuario(usuario: Usuario) {
        usuarioRepository?.save(usuario)
        usuario.user?.let {
            userService?.saveUser(it)
        }
    }

    @Transactional(readOnly = true)
    fun comprobarSiNombreUsuarioExiste(nombreUsuario: String?): Boolean {
        var res = false
        var usuarios = usuarioRepository?.findAll()
        if (usuarios != null) {
            for (u in usuarios) {
                if (u.user?.username.equals(nombreUsuario)) {
                    res = true
                }
            }
        }
        return res
    }

    @Transactional(readOnly = true)
    fun comprobarSiEmailExiste(email: String?): Boolean {
        var res = false
        var usuarios = usuarioRepository?.findAll()
        if (usuarios != null) {
            for (u in usuarios) {
                if (u.email.equals(email)) {
                    res = true
                }
            }
        }
        return res
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscarUsuarioPorId(idUsuario: Int): Usuario? {
        return usuarioRepository?.findById(idUsuario)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscarUsuarioPorNombreUsuario(username: String): Usuario? {
        return usuarioRepository?.buscarUsuarioPorNombreUsuario(username)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscarLigasUsuario(username: String): Collection<Liga>? {
        return usuarioRepository?.buscarLigasUsuario(username)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscarTodosUsuarios(): MutableList<String?>? {
        return usuarioRepository?.findAll()?.stream()?.map { x -> x.user?.username }?.collect(Collectors.toList())
    }

}

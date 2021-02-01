package org.springframework.samples.futgol.usuario

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.liga.Liga
import org.springframework.samples.futgol.login.AuthoritiesServicio
import org.springframework.samples.futgol.login.UserServicio
import org.springframework.transaction.annotation.Transactional


@Service
class UsuarioServicio {

    private var usuarioRepository: UsuarioRepository? = null

    @Autowired
    private val userService: UserServicio? = null

    @Autowired
    private val authoritiesService: AuthoritiesServicio? = null

    @Autowired
    fun UsuarioServicio(usuarioRepository: UsuarioRepository?) {
        this.usuarioRepository = usuarioRepository
    }

    @Transactional
    @Throws(DataAccessException::class)
    fun saveUsuario(usuario: Usuario) {
        usuarioRepository?.save(usuario)
        usuario.user?.let {
            userService?.saveUser(it)
        }
//        usuario.user?.username?.let { authoritiesService?.saveAuthorities(it, "usuario") }
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun findAllUsuarios(): Collection<Usuario?>? {
        return usuarioRepository?.findAll()
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
    fun buscarUsuarioPorId(id: Int): Usuario? {
        return usuarioRepository?.buscarUsuarioPorId(id)
    }

}

package org.springframework.samples.futgol.usuario

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.liga.Liga
import org.springframework.samples.futgol.login.AuthoritiesServicio
import org.springframework.samples.futgol.login.UserServicio
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.Principal
import java.util.stream.Collectors

@Service
class UsuarioServicio {

    private var usuarioRepositorio: UsuarioRepositorio? = null

    @Autowired
    private val userService: UserServicio? = null

    @Autowired
    private val authoritiesServicio: AuthoritiesServicio? = null

    @Autowired
    fun UsuarioServicio(usuarioRepositorio: UsuarioRepositorio?) {
        this.usuarioRepositorio = usuarioRepositorio
    }

    fun usuarioLogueado(principal: Principal): Usuario? {
        val username: String = principal.name
        return buscarUsuarioPorNombreUsuario(username)
    }

    @Transactional
    @Throws(DataAccessException::class)
    fun guardarUsuario(usuario: Usuario) {
        usuarioRepositorio?.save(usuario)
    }

    @Transactional(readOnly = true)
    fun comprobarSiNombreUsuarioExiste(nombreUsuario: String?): Boolean? {
        return usuarioRepositorio?.findAll()?.stream()?.anyMatch { x -> x.user?.username.equals(nombreUsuario) }
    }

    @Transactional(readOnly = true)
    fun comprobarSiEmailExiste(email: String?): Boolean? {
        return usuarioRepositorio?.findAll()?.stream()?.anyMatch { x -> x.email == email }

    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscarUsuarioPorNombreUsuario(username: String): Usuario? {
        return usuarioRepositorio?.buscarUsuarioPorNombreUsuario(username)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscarLigasUsuario(username: String): Collection<Liga>? {
        return usuarioRepositorio?.buscarLigasUsuario(username)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscarTodosUsuarios(): MutableList<String?>? {
        return usuarioRepositorio?.findAll()?.stream()?.map { x -> x.user?.username }?.collect(Collectors.toList())
    }

    @Transactional(readOnly = true)
    fun ligasAInvitar(username: String, principal: Principal): MutableList<Liga> {
        val ligasUsuario = buscarLigasUsuario(username)
        var ligasNoUsuario: MutableList<Liga> = ArrayList()
        val misLigas = usuarioLogueado(principal)?.user?.let { buscarLigasUsuario(it.username) }

        if (misLigas != null && ligasUsuario != null) {
            for (ligaM in misLigas) {
                var res = true
                for (ligaU in ligasUsuario) {
                    if (ligaM.name.equals(ligaU.name)) {
                        res = false
                        break
                    }
                }
                if (res && ligaM.equipos.size <= 8) {
                    ligasNoUsuario.add(ligaM)
                }
            }
        }
        return ligasNoUsuario
    }

}

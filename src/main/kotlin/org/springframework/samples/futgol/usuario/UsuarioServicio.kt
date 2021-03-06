package org.springframework.samples.futgol.usuario

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.liga.Liga
import org.springframework.samples.futgol.login.AuthoritiesServicio
import org.springframework.samples.futgol.login.UserServicio
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.Principal

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
        return nombreUsuario?.let { usuarioRepositorio?.existeUsuario(it) }
    }

    @Transactional(readOnly = true)
    fun comprobarSiEmailExiste(email: String?): Boolean? {
        return email?.let { usuarioRepositorio?.existeUsuarioConEmail(it) }
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
    fun buscarInvitacionesUsuario(username: String): Collection<Liga>? {
        return usuarioRepositorio?.buscarInvitacionesUsuario(username)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    fun buscarTodosNombresUsuarios(): MutableList<String>? {
        return usuarioRepositorio?.buscaTodosNombresusuario()
    }

    @Transactional(readOnly = true)
    fun ligasAInvitar(username: String, principal: Principal): MutableList<Liga> {
        val ligasUsuario = buscarLigasUsuario(username)
        var ligasNoUsuario: MutableList<Liga> = ArrayList()
        val misLigas = usuarioLogueado(principal)?.user?.let { buscarLigasUsuario(it.username) }
        val ligasInvitado = buscarInvitacionesUsuario(username)

        if (misLigas != null && ligasUsuario != null && ligasInvitado != null) {
            for (ligaM in misLigas) {
                var resLig = true
                var resInv = true
                for (ligaU in ligasUsuario) {
                    if (ligaM.name.equals(ligaU.name)) {
                        resLig = false
                        break
                    }
                }
                for (ligaI in ligasInvitado) {
                    if (ligaM.name.equals(ligaI.name)) {
                        resLig = false
                        break
                    }
                }
                if (resLig && resInv && ligaM.equipos.size <= 8) {
                    ligasNoUsuario.add(ligaM)
                }
            }
        }
        return ligasNoUsuario
    }

}

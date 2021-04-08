package org.springframework.samples.futgol.login

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class AuthoritiesServicio {

    private var authoritiesRepositorio: AuthoritiesRepositorio? = null

    private var userServicio: UserServicio? = null

    @Autowired
    fun AuthoritiesServicio(authoritiesRepositorio: AuthoritiesRepositorio, userServicio: UserServicio?) {
        this.authoritiesRepositorio = authoritiesRepositorio
        this.userServicio = userServicio
    }

    @Transactional
    @Throws(DataAccessException::class)
    fun guardarAutoridades(username: String, role: String?) {
        val authority = Authorities()
        val user: User? = this.userServicio?.buscarUser(username)
        if (user != null) {
            authority.user = user
            authority.authority = role
            authoritiesRepositorio?.save(authority)
        } else throw object : DataAccessException("User '$username' not found!") {}
    }

}

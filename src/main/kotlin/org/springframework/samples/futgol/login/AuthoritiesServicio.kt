package org.springframework.samples.futgol.login

import org.springframework.stereotype.Service
import org.springframework.dao.DataAccessException
import org.springframework.transaction.annotation.Transactional
import org.springframework.beans.factory.annotation.Autowired
import java.util.*


@Service
class AuthoritiesServicio {

    private var authoritiesRepositorio: AuthoritiesRepositorio? = null

    private var userService: UserServicio? = null

    @Autowired
    fun AuthoritiesService(authoritiesRepository: AuthoritiesRepositorio, userService: UserServicio?) {
        authoritiesRepositorio = authoritiesRepository
        this.userService = userService
    }

    @Transactional
    @Throws(DataAccessException::class)
    fun saveAuthorities(username: String, role: String?) {
        val authority = Authorities()
        val user: User? = userService?.findUser(username)
        if (user != null) {
            authority.user = user
            authority.authority = role
            authoritiesRepositorio?.save(authority)
        } else throw object : DataAccessException("User '$username' not found!") {}
    }

    @Transactional
    @Throws(DataAccessException::class)
    fun saveAuthorities2(authority: Authorities) {
            authoritiesRepositorio?.save(authority)
    }

    @Transactional(readOnly = true)
    fun findAuthority(username: String?): Authorities? {
        return username?.let { authoritiesRepositorio?.findByUserName(it) }
    }
}

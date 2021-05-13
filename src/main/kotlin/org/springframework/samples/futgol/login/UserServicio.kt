package org.springframework.samples.futgol.login

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServicio {

    private var userRepositorio: UserRepositorio? = null

    @Autowired
    fun UserServicio(userRepositorio: UserRepositorio?) {
        this.userRepositorio = userRepositorio
    }

    @Transactional
    @Throws(DataAccessException::class)
    fun guardarUser(user: User, nuevaPass: Boolean) {
        user.enabled = true
        if (nuevaPass) {
            var contrasenyaEncrip = BCryptPasswordEncoder().encode(user.password)
            user.password = contrasenyaEncrip
        }
        userRepositorio?.save(user)
    }

    @Transactional(readOnly = true)
    fun buscarUser(username: String?): User? {
        return username?.let { userRepositorio?.findByNombreUsuario(it) }
    }
}

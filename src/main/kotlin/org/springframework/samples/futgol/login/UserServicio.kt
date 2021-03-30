package org.springframework.samples.futgol.login

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServicio {

    private var userRepository: UserRepositorio? = null

    @Autowired
    fun UserService(userRepository: UserRepositorio?) {
        this.userRepository = userRepository
    }

    @Transactional
    @Throws(DataAccessException::class)
    fun saveUser(user: User, nuevaPass: Boolean) {
        user.enabled = true
        if (nuevaPass) {
            var contrasenyaEncrip = BCryptPasswordEncoder().encode(user.password)
            user.password = contrasenyaEncrip
        }
        userRepository?.save(user)
    }

    @Transactional(readOnly = true)
    fun findUser(username: String?): User? {
        return username?.let { userRepository?.findByNombreUsuario(it) }
    }
}

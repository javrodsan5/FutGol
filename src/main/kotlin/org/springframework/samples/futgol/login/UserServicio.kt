package org.springframework.samples.futgol.login

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class UserServicio {

    private var userRepository: UserRepositorio? = null
    private lateinit var passwordEncoder: PasswordEncoder


    @Autowired
    fun UserService(userRepository: UserRepositorio?, passwordEncoder: PasswordEncoder) {
        this.userRepository = userRepository
        this.passwordEncoder = passwordEncoder
    }

    @Transactional
    @Throws(DataAccessException::class)
    fun saveUser(user: User) {
        user.enabled = true
        if (passwordEncoder != null) {
            print("xd")
            user.password = passwordEncoder.encode(user.password)
        }
        userRepository?.save(user)
    }

    @Transactional(readOnly = true)
    fun findUser(username: String?): User? {
        return username?.let { userRepository?.findByNombreUsuario(it) }
    }
}

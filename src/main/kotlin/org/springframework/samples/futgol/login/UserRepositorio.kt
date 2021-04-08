package org.springframework.samples.futgol.login

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository

interface UserRepositorio : Repository<User, Int> {

    fun save(user: User)

    @Query("SELECT u FROM User u where u.username = ?1")
    fun findByNombreUsuario(user: String): User


}

package org.springframework.samples.futgol.login

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository

interface AuthoritiesRepositorio : Repository<Authorities, Int> {

    fun save(authority: Authorities)

    @Query("SELECT a FROM Authorities a where a.user.username = ?1")
    fun findByUserName(username: String): Authorities


}

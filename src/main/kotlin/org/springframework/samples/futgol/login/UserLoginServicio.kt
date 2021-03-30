package org.springframework.samples.futgol.login

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserLoginServicio : UserDetailsService {

    @Autowired
    private val userService: UserServicio? = null

    override fun loadUserByUsername(username: String?): UserDetails {
        var user: User? =
            userService?.findUser(username) ?: throw UsernameNotFoundException("Usuario " + username + "no encontrado.")

        var grantedAuthorities = ArrayList<GrantedAuthority>()
        grantedAuthorities.add(SimpleGrantedAuthority("usuario"))
        return org.springframework.security.core.userdetails.User(user?.username, user?.password, grantedAuthorities)
    }

}

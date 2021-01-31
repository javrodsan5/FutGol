package org.springframework.samples.futgol.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.password.NoOpPasswordEncoder

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import javax.sql.DataSource


@Configuration
@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    var dataSource: DataSource? = null

    override protected fun configure(http: HttpSecurity) {
          http.authorizeRequests()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers(HttpMethod.GET, "/","/oups").permitAll()
                .antMatchers("/owners/**").denyAll()
                .antMatchers("/usuarios/**").permitAll()
              .antMatchers("/liga/**").permitAll()
                .and()
                .formLogin()
                .loginPage("/login.html")
                .failureUrl("/login-error")
                .and()
              .logout()
              .logoutRequestMatcher(AntPathRequestMatcher("/logout"))
              .logoutSuccessUrl("/")
              .invalidateHttpSession(true)
              .deleteCookies("JSESSIONID")

        http.csrf().ignoringAntMatchers("/usuarios/**");
        http.csrf().ignoringAntMatchers("/liga/**");

        http.csrf().ignoringAntMatchers("/h2-console/**");
        http.headers().frameOptions().sameOrigin();
    }


    override fun configure(auth: AuthenticationManagerBuilder): Unit {
        auth.jdbcAuthentication().dataSource(dataSource)
                .usersByUsernameQuery(
                        "select username,password,enabled "
                                + "from users "
                                + "where username = ?")
                .authoritiesByUsernameQuery(
                        "select username, authority "
                                + "from authorities "
                                + "where username = ?")
                .passwordEncoder(passwordEncoder())
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder? {
        return NoOpPasswordEncoder.getInstance()
    }
}

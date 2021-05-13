package org.springframework.samples.futgol.usuario

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class UsuarioRepositorioTest(@Autowired private val usuarioRepositorio: UsuarioRepositorio) {

    @Test
    fun buscaUsuarioPorIdTest() {
        var usuario = this.usuarioRepositorio.buscarUsuarioPorId(14)
        Assertions.assertThat(usuario.user?.username).isEqualTo("fercadu")
    }

    @Test
    fun buscaUsuarioPorNombreUsuarioTest() {
        var usuario = this.usuarioRepositorio.buscarUsuarioPorNombreUsuario("ferki")
        Assertions.assertThat(usuario.name).isEqualTo("Fernando")
    }

    @Test
    fun buscaLigasUsuarioTest() {
        var ligas = this.usuarioRepositorio.buscarLigasUsuario("ferki")
        Assertions.assertThat(ligas.size).isEqualTo(2)
    }

    @Test
    fun buscaTodosNombresusuarioTest() {
        var nombresUsuario = this.usuarioRepositorio.buscaTodosNombresusuario()
        Assertions.assertThat(nombresUsuario.size).isEqualTo(4)
        Assertions.assertThat(nombresUsuario.contains("fercadu")).isEqualTo(true)

    }

    @Test
    fun existeUsuarioTest() {
        var b = this.usuarioRepositorio.existeUsuario("woma")
        Assertions.assertThat(b).isEqualTo(false)

    }

    @Test
    fun existeUsuarioConEmailTest() {
        var b = this.usuarioRepositorio.existeUsuarioConEmail("javier@hotmail.com")
        Assertions.assertThat(b).isEqualTo(true)

    }

    @Test
    fun buscarInvitacionesUsuarioTest() {
        var invitaciones = this.usuarioRepositorio.buscarInvitacionesUsuario("javier")
        Assertions.assertThat(invitaciones.size).isEqualTo(0)

    }
}

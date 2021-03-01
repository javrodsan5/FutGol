package org.springframework.samples.futgol.usuario

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class UsuarioRepositorioTest(@Autowired private val usuarioRepository: UsuarioRepository) {

    @Test
    fun buscaUsuarioPorIdTest() {
        var usuario = this.usuarioRepository.buscarUsuarioPorId(1)
        if (usuario != null) Assertions.assertThat(usuario.user?.username).isEqualTo("administrador1")
    }

    @Test
    fun buscaUsuarioPorNombreUsuarioTest() {
        var usuario = this.usuarioRepository.buscarUsuarioPorNombreUsuario("ferki")
        if (usuario != null) Assertions.assertThat(usuario.name).isEqualTo("Fernando")
    }

    @Test
    fun buscaLigasUsuarioTest() {
        var ligas = this.usuarioRepository.buscarLigasUsuario("ferki")
        if (ligas != null) Assertions.assertThat(ligas.size).isEqualTo(2)
    }
}

package org.springframework.samples.futgol.usuario

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class UsuarioRepositorioTest(@Autowired private val usuarios: UsuarioRepository) {

    @Test
    fun buscaUsuarioPorIdTest() {
        var usuario = this.usuarios.buscarUsuarioPorId(1)
        if (usuario != null) Assertions.assertThat(usuario.user?.username).isEqualTo("administrador1")
    }

    @Test
    fun buscaUsuarioPorNombreUsuarioTest() {
        var usuario = this.usuarios.buscarUsuarioPorNombreUsuario("ferki")
        if (usuario != null) Assertions.assertThat(usuario.name).isEqualTo("Fernando")
    }

    @Test
    fun buscaLigasUsuarioTest() {
        var ligas = this.usuarios.buscarLigasUsuario("ferki")
        if (ligas != null) Assertions.assertThat(ligas.size).isEqualTo(2)
    }
}

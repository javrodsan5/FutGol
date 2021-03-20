package org.springframework.samples.futgol.jugador

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JugadorRepositorioTest(@Autowired private val jugadorRepositorio: JugadorRepositorio) {

    @Test
    fun buscaJugadorPorIdTest() {
        val jugador = jugadorRepositorio.findById(1608)
        Assertions.assertThat(jugador.name).isEqualTo("Jan Oblak")
    }

    @Test
    fun buscaJugadorPorNombreYEquipoTest() {
        val jugador = jugadorRepositorio.buscarJugadorPorNombreyEquipo("Jan Oblak", "Atlético Madrid")
        Assertions.assertThat(jugador.id).isEqualTo(1608)
    }
}

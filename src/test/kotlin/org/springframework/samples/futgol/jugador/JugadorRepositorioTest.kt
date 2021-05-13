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
        val jugador = jugadorRepositorio.findById(3131)
        Assertions.assertThat(jugador.name).isEqualTo("Jan Oblak")
    }

    @Test
    fun buscaJugadorPorNombreYEquipoTest() {
        val jugador = jugadorRepositorio.buscarJugadorPorNombreyEquipo("Jan Oblak", "Atlético Madrid")
        Assertions.assertThat(jugador.id).isEqualTo(3131)
    }

    @Test
    fun existeJugadorIdTest() {
        val b= jugadorRepositorio.existeJugadorId(3131)
        Assertions.assertThat(b).isEqualTo(true)
    }

    @Test
    fun existeJugadorNombreTest() {
        val b= jugadorRepositorio.existeJugadorNombre("Sime Vrsaljko")
        Assertions.assertThat(b).isEqualTo(true)
    }

    @Test
    fun existeJugadorEquipoTest() {
        val b= jugadorRepositorio.existeJugadorEquipo("Thibaut Courtois", "Real Madrid")
        Assertions.assertThat(b).isEqualTo(true)
    }

    @Test
    fun buscarTodosJugadoresTest() {
        val jugadores = jugadorRepositorio.findAll()
        Assertions.assertThat(jugadores.size).isEqualTo(493)
    }

    @Test
    fun buscarJugadoresOrdenPuntosTest() {
        val jugadores = jugadorRepositorio.findAll()
        val jugadoresOrdenados = jugadorRepositorio.buscarJugadoresOrdenPuntos()
        Assertions.assertThat(jugadores.sortedBy { x-> -x.puntos }).isEqualTo(jugadoresOrdenados)
    }

    @Test
    fun buscarJugadoresSinPosicionTest() {
        val jugadores = jugadorRepositorio.buscarJugadoresSinPosicion()
        Assertions.assertThat(jugadores.size).isEqualTo(0)
    }

    @Test
    fun eliminarJugadorTest() {
        val b1= jugadorRepositorio.existeJugadorId(3131)
        Assertions.assertThat(b1).isEqualTo(true)
        jugadorRepositorio.deleteJugadorById(3131)
        val b2= jugadorRepositorio.existeJugadorId(3131)
        Assertions.assertThat(b2).isEqualTo(false)
    }


}

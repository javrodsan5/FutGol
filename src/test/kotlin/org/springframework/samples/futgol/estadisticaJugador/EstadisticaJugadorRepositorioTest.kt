package org.springframework.samples.futgol.estadisticaJugador

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EstadisticaJugadorRepositorioTest(@Autowired private val estadisticaJugadorRepositorio: EstadisticaJugadorRepositorio) {

    @Test
    fun existenEstadisticasTest() {
        Assertions.assertThat(this.estadisticaJugadorRepositorio.existeAlgunaEstadistica())
    }

    @Test
    fun existeEstadisticaJugadorTest() {
        Assertions.assertThat(this.estadisticaJugadorRepositorio.tieneAlgunaEstadisticaJugador(3131))
    }

    @Test
    fun existeEstadisticaJugEqPartTest() {
        Assertions.assertThat(
            this.estadisticaJugadorRepositorio.existeEstadisticaJugEqPart(
                "Jan Oblak",
                "Atlético Madrid",
                5341
            )
        )
    }

    @Test
    fun buscaEstadisticaPorPartidoTest() {
        Assertions.assertThat(this.estadisticaJugadorRepositorio.buscarEstadisticasPorPartido(5341).isNotEmpty())
    }

    @Test
    fun buscaEstadisticaPorJornadaTest() {
        Assertions.assertThat(this.estadisticaJugadorRepositorio.buscarEstadisticasPorJornada(13).isNotEmpty())
    }

    @Test
    fun existeEstadisticaJugJornTest() {
        Assertions.assertThat(this.estadisticaJugadorRepositorio.existeEstadisticaJugadorJornada(3131, 4))
    }

}

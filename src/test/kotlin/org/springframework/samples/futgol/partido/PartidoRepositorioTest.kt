package org.springframework.samples.futgol.partido

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PartidoRepositorioTest(@Autowired private val partidoRepositorio: PartidoRepositorio) {

    @Test
    fun buscarPartidoPorNombresEquiposTest() {
        var partido = this.partidoRepositorio.buscarPartidoPorNombresEquipos("Osasuna","Atlético Madrid")
        Assertions.assertThat(partido.id).isEqualTo(5387)
    }

    @Test
    fun existePartidoTest() {
        var b = this.partidoRepositorio.existePartido("Osasuna","Atlético Madrid")
        Assertions.assertThat(b).isEqualTo(true)
    }

    @Test
    fun buscarTodosPartidosTest() {
        var partidos = this.partidoRepositorio.findAll()
        Assertions.assertThat(partidos.size).isEqualTo(380)
    }

    @Test
    fun buscarPartidoPorIDTest() {
        var partido = this.partidoRepositorio.findById(5327)
        Assertions.assertThat(partido.fecha).isEqualTo("2020-09-13")
    }

}

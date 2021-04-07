package org.springframework.samples.futgol.jornada

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.samples.futgol.jornadas.JornadaRepositorio

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class JornadaRepositorioTest(@Autowired private val jornadaRepositorio: JornadaRepositorio) {

    @Test
    fun buscaJornadaPorNumeroJornadaTest() {
        val jornada = jornadaRepositorio.findJornadaByNumeroJornada(11)
        Assertions.assertThat(jornada.id).isEqualTo(11)
    }

    @Test
    fun existeJornadaTest() {
        val b = jornadaRepositorio.existeJornada(1)
        Assertions.assertThat(b).isEqualTo(true)
    }

    @Test
    fun buscarJornadasTest() {
        val jornadas = jornadaRepositorio.findAll()
        Assertions.assertThat(jornadas.size).isEqualTo(38)
    }

    @Test
    fun buscaJornadaPorIDTest() {
        val jornada = jornadaRepositorio.findJornadaById(1)
        Assertions.assertThat(jornada.id).isEqualTo(1)
    }

}

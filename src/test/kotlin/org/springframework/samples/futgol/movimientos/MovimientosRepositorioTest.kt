package org.springframework.samples.futgol.movimientos

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class MovimientosRepositorioTest(@Autowired private val movimientoRepositorio: MovimientoRepositorio) {

    @Test
    fun existenMovimientosLigaTest() {
        Assertions.assertThat(this.movimientoRepositorio.existenMovimientosLiga(35))
    }

    @Test
    fun buscanMovimientosLigaTest() {
        Assertions.assertThat(this.movimientoRepositorio.buscarMovimientosDeLigaPorId(35).size==1)
    }

}

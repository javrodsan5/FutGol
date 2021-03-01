package org.springframework.samples.futgol.equipoReal

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class EquipoRealRepositorioTest(@Autowired private val equipoRealRepositorio: EquipoRealRepositorio) {

    @Test
    fun buscaUsuarioPorIdTest() {
        var equipoReal = this.equipoRealRepositorio.buscarEquipoRealPorNombre("Osasuna")
        Assertions.assertThat(equipoReal!!.id).isEqualTo(93)
    }
}

package org.springframework.samples.futgol.clausula

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class ClausulaRepositorioTest(@Autowired private val clausulaRepositorio: ClausulaRepositorio) {

    @Test
    fun buscaEquipoPorIdTest() {
        val clausulas = clausulaRepositorio.findClausulasByJugadorId(1608)
        Assertions.assertThat(clausulas.size).isEqualTo(2)
    }
}

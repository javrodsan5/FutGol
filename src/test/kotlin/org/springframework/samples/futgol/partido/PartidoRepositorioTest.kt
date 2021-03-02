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
    fun buscaLigaPorNombreTest() {
        var partido = this.partidoRepositorio.buscarPartidoPorNombresEquipos("Osasuna","Atlético Madrid")
        Assertions.assertThat(partido.resultado).isEqualTo("1-3")
    }
}

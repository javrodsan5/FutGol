package org.springframework.samples.futgol.liga

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class LigaRepositorioTest(@Autowired private val ligas: LigaRepositorio) {

    @Test
    fun buscaLigaPorNombreTest() {
        var liga = this.ligas.findLigaByName("miligaFerki")
        if (liga != null) Assertions.assertThat(liga.admin?.user?.username).isEqualTo("administrador1")
    }

    @Test
    fun buscaLigaPorIdTest() {
        var liga = this.ligas.buscarLigaPorId(1)
        if (liga != null) Assertions.assertThat(liga.admin?.user?.username).isEqualTo("ferki")
    }

    @Test
    fun buscaTodasLigasTest() {
        var ligas = this.ligas.findAll()
        if (ligas != null) Assertions.assertThat(ligas.size).isEqualTo(4)
    }


}

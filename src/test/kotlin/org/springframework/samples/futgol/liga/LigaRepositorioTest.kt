package org.springframework.samples.futgol.liga

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class LigaRepositorioTest(@Autowired private val ligaRepositorio: LigaRepositorio) {

    @Test
    fun buscaLigaPorNombreTest() {
        var liga = this.ligaRepositorio.findLigaByName("miligaFerki2")
        if (liga != null) Assertions.assertThat(liga.admin?.user?.username).isEqualTo("administrador1")
    }

    @Test
    fun buscaLigaPorIdTest() {
        var liga = this.ligaRepositorio.buscarLigaPorId(1)
        if (liga != null) Assertions.assertThat(liga.admin?.user?.username).isEqualTo("ferki")
    }

    @Test
    fun buscaTodasLigasTest() {
        var ligas = this.ligaRepositorio.findAll()
        if (ligas != null) Assertions.assertThat(ligas.size).isEqualTo(11)
    }
}

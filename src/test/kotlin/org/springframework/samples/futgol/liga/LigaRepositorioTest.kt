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
        var liga = this.ligaRepositorio.findLigaByName("Ferki Liga")
        Assertions.assertThat(liga.admin?.user?.username).isEqualTo("ferki")
    }

    @Test
    fun buscaLigaPorIdTest() {
        var liga = this.ligaRepositorio.buscarLigaPorId(35)
        Assertions.assertThat(liga.admin?.user?.username).isEqualTo("javi")
    }

    @Test
    fun buscaTodasLigasTest() {
        var ligas = this.ligaRepositorio.findAll()
        Assertions.assertThat(ligas.size).isEqualTo(3)
    }

    @Test
    fun comprobarSiExisteLigaTest() {
        var b = this.ligaRepositorio.comprobarSiExisteLiga("Javi Liga")
        Assertions.assertThat(b).isEqualTo(true)
    }

    @Test
    fun comprobarSiExisteLiga2Test() {
        var b = this.ligaRepositorio.comprobarSiExisteLiga2(33)
        Assertions.assertThat(b).isEqualTo(true)
    }
}

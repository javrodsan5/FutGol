package org.springframework.samples.futgol.equipo

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class EquipoRepositorioTest(@Autowired private val equipoRepositorio: EquipoRepositorio) {

    @Test
    fun buscaEquipoPorIdTest() {
        val equipo = equipoRepositorio.buscaEquiposPorId(114)
        Assertions.assertThat(equipo.name).isEqualTo("equipoteke")
    }

    @Test
    fun buscaEquipoPorNombreTest() {
        val equipo = equipoRepositorio.buscarEquipoPorNombre("equipoteke")
        Assertions.assertThat(equipo.id).isEqualTo(114)
    }

    @Test
    fun buscaEquiposLigaTest() {
        val equiposLiga = equipoRepositorio.buscarEquiposDeLigaPorId(11)
        Assertions.assertThat(equiposLiga.size).isEqualTo(8)
    }
}

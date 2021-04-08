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
    fun buscarEquipoPorNombreYLigaTest() {
        val equipo = equipoRepositorio.buscarEquipoPorNombreYLiga("Fercadu Team",33)
        Assertions.assertThat(equipo.usuario?.user?.username).isEqualTo("fercadu")
    }

    @Test
    fun buscarEquipoUsuarioEnLigaTest() {
        val equipo = equipoRepositorio.buscarEquipoUsuarioEnLiga("javi",33)
        Assertions.assertThat(equipo.id).isEqualTo(35)
    }

    @Test
    fun buscarEquiposDeLigaTest() {
        val equiposLiga = equipoRepositorio.buscarEquiposDeLigaPorId(34)
        Assertions.assertThat(equiposLiga.size).isEqualTo(1)
    }

    @Test
    fun buscaEquiposPorIdTest() {
        val equipo = equipoRepositorio.buscaEquiposPorId(36)
        Assertions.assertThat(equipo.name).isEqualTo("JAVIR ")
    }

    @Test
    fun tengoEquipoLigaTest() {
        Assertions.assertThat(equipoRepositorio.tengoEquipoLiga(35, 16))
    }

    @Test
    fun existeEquipoTest() {
        val b = equipoRepositorio.existeEquipo(34)
        Assertions.assertThat(b).isEqualTo(true)
    }

    @Test
    fun existeUsuarioConEquipoEnLigaTest() {
        val b = equipoRepositorio.existeUsuarioConEquipoEnLiga("woma",34)
        Assertions.assertThat(b).isEqualTo(false)
    }

    @Test
    fun buscarTodosEquiposTest() {
        val equipos = equipoRepositorio.findAll()
        Assertions.assertThat(equipos.size).isEqualTo(6)
    }

}

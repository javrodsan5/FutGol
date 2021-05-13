package org.springframework.samples.futgol.puntosJornadaEquipo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PtosJornEqRepositorioTest(@Autowired private val ptosJornadaEquipoRepositorio: PtosJornadaEquipoRepositorio){



}

package org.springframework.samples.futgol.jornada

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.equipoReal.EquipoRealServicio
import org.springframework.samples.futgol.estadisticaJugador.EstadisticaJugadorServicio
import org.springframework.samples.futgol.jornadas.JornadaControlador
import org.springframework.samples.futgol.jornadas.JornadaServicio
import org.springframework.samples.futgol.jugador.JugadorControlador
import org.springframework.samples.futgol.jugador.JugadorServicio
import org.springframework.samples.futgol.partido.PartidoServicio
import org.springframework.samples.futgol.usuario.UsuarioServicio
import org.springframework.security.config.annotation.web.WebSecurityConfigurer
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(
    JornadaControlador::class, excludeFilters = [ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = arrayOf(WebSecurityConfigurer::class)
    )]
)
@AutoConfigureMockMvc(addFilters = false)
class JornadaControladorTest {


    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var jornadaServicio: JornadaServicio

    @MockBean
    private lateinit var equipoRealServicio: EquipoRealServicio

    @MockBean
    private lateinit var partidoServicio: PartidoServicio

    @MockBean
    private lateinit var estadisticaJugadorServicio: EstadisticaJugadorServicio

    @Test
    fun testJornadas() {
        mockMvc.perform(MockMvcRequestBuilders.get("/jornadas"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("jornadas/detallesJornada"))
    }
}

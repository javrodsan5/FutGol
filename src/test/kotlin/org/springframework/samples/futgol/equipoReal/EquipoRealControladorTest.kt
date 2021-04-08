package org.springframework.samples.futgol.equipoReal

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.samples.futgol.jornadas.JornadaServicio
import org.springframework.samples.futgol.jugador.JugadorServicio
import org.springframework.security.config.annotation.web.WebSecurityConfigurer
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@WebMvcTest(
    EquipoRealControlador::class, excludeFilters = [ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = arrayOf(WebSecurityConfigurer::class)
    )]
)
@AutoConfigureMockMvc(addFilters = false)
class EquipoRealControladorTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var jornadaServicio: JornadaServicio

    @MockBean
    private lateinit var jugadorServicio: JugadorServicio


    @MockBean
    private lateinit var equipoRealServicio: EquipoRealServicio

    @Test
    fun testListaEquiposReales() {
        mockMvc.perform(MockMvcRequestBuilders.get("/equiposLiga"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("equiposReales/listaEquiposReales"))
    }

    @Test
    fun testDetallesEquipoReal() {
        mockMvc.perform(MockMvcRequestBuilders.get("/equiposLiga/{nombreEquipo}", "Sevilla"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("equiposReales/detallesEquipoReal"))
    }


}

package org.springframework.samples.futgol.jugador

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.samples.futgol.clausula.ClausulaServicio
import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.equipoReal.EquipoRealServicio
import org.springframework.samples.futgol.liga.LigaServicio
import org.springframework.samples.futgol.usuario.UsuarioServicio
import org.springframework.security.config.annotation.web.WebSecurityConfigurer
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view

@WebMvcTest(
    JugadorControlador::class, excludeFilters = [ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = arrayOf(WebSecurityConfigurer::class)
    )]
)
@AutoConfigureMockMvc(addFilters = false)
class JugadorControladorTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var usuarioServicio: UsuarioServicio

    @MockBean
    private lateinit var jugadorServicio: JugadorServicio

    @MockBean
    private lateinit var equipoServicio: EquipoServicio

    @MockBean
    private lateinit var equipoRealServicio: EquipoRealServicio

    @MockBean
    private lateinit var ligaServicio: LigaServicio

    @MockBean
    private lateinit var clausulaServicio: ClausulaServicio

    @Test
    fun testDetallesJugador() {
        mockMvc.perform(get("/jugador/{idJugador}", "2139"))
            .andExpect(status().isOk)
            .andExpect(view().name("jugadores/detallesJugador"))
    }

    @Test
    fun testTopJugadores() {
        mockMvc.perform(get("/topJugadores"))
            .andExpect(status().isOk)
            .andExpect(view().name("jugadores/buscaJugador"))
    }

    //FALLO CSRF
//    @Test
//    fun testClausulaJugador() {
//        mockMvc.perform(get("/equipo/{idEquipo}/jugador/{idJugador}/clausula", "114","1608"))
//            .andExpect(status().isOk)
//            .andExpect(view().name("/jugadores/detallesJugadorEquipo"))
//    }
}

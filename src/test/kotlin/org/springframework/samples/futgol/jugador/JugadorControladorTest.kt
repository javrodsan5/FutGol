package org.springframework.samples.futgol.jugador

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.liga.LigaServicio
import org.springframework.samples.futgol.login.AuthoritiesServicio
import org.springframework.samples.futgol.login.UserServicio
import org.springframework.samples.futgol.usuario.UsuarioController
import org.springframework.samples.futgol.usuario.UsuarioServicio
import org.springframework.security.config.annotation.web.WebSecurityConfigurer
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(
    UsuarioController::class, excludeFilters = [ComponentScan.Filter(
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
    private lateinit var userServicio: UserServicio

    @MockBean
    private lateinit var authoritiesServicio: AuthoritiesServicio

    @MockBean
    private lateinit var ligaServicio: LigaServicio

    @Test
    fun testDetallesJugador() {
        mockMvc.perform(MockMvcRequestBuilders.get("/jugador/2116"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("/jugadores/detallesJugador"))
    }
}

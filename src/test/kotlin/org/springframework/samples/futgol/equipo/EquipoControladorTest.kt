package org.springframework.samples.futgol.equipo

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.samples.futgol.jugador.JugadorServicio
import org.springframework.samples.futgol.liga.LigaServicio
import org.springframework.samples.futgol.usuario.UsuarioServicio
import org.springframework.security.config.annotation.web.WebSecurityConfigurer
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(
    EquipoControlador::class, excludeFilters = [ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = arrayOf(WebSecurityConfigurer::class)
    )]
)
@AutoConfigureMockMvc(addFilters = false)
class EquipoControladorTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var usuarioServicio: UsuarioServicio

    @MockBean
    private lateinit var jugadorServicio: JugadorServicio

    @MockBean
    private lateinit var equipoServicio: EquipoServicio

    @MockBean
    private lateinit var ligaServicio: LigaServicio


    @Test
    fun testProcessCreationEquipoFormSuccess() {
        mockMvc.perform(
            post("/liga/{idLiga}/nuevoEquipo", "1")
                .param("name", "EquipoTest")
        ).andExpect(status().is3xxRedirection)
    }

}

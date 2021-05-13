package org.springframework.samples.futgol.liga

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
import org.springframework.samples.futgol.usuario.UsuarioServicio
import org.springframework.security.config.annotation.web.WebSecurityConfigurer
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@WebMvcTest(
    LigaControlador::class, excludeFilters = [ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = arrayOf(WebSecurityConfigurer::class)
    )])
@AutoConfigureMockMvc(addFilters = false)
class LigaControladorTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var usuarioServicio: UsuarioServicio

    @MockBean
    private lateinit var equipoServicio: EquipoServicio

    @MockBean
    private lateinit var equipoRealServicio: EquipoRealServicio

    @MockBean
    private lateinit var clausulaServicio: ClausulaServicio

    @MockBean
    private lateinit var ligaServicio: LigaServicio

    @Test
    fun testProcessCreationFormSuccess() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/liga/crearEditarLiga")
                .param("name", "LiguillaPrueba")
        ).andExpect(MockMvcResultMatchers.status().is3xxRedirection)
    }

    @Test
    fun testClasificacionGeneral() {
        mockMvc.perform(MockMvcRequestBuilders.get("/topUsuarios"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("usuarios/rankingUsuarios"))
    }
}

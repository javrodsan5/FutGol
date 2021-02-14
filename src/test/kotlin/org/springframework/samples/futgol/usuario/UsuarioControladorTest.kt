package org.springframework.samples.futgol.usuario
//
//import org.junit.jupiter.api.Test
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration
//import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
//import org.springframework.boot.test.mock.mockito.MockBean
//import org.springframework.context.annotation.ComponentScan
//import org.springframework.context.annotation.FilterType
//import org.springframework.samples.futgol.liga.LigaServicio
//import org.springframework.samples.futgol.login.AuthoritiesServicio
//import org.springframework.samples.futgol.login.UserServicio
//import org.springframework.security.config.annotation.web.WebSecurityConfigurer
//import org.springframework.test.web.servlet.MockMvc
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers
//
//@WebMvcTest(
//    UsuarioController::class, excludeFilters = [ComponentScan.Filter(
//        type = FilterType.ASSIGNABLE_TYPE,
//        classes = arrayOf(WebSecurityConfigurer::class)
//    )]
//)
//@EnableAutoConfiguration(
//    exclude = SecurityAutoConfiguration.class)
//    class UsuarioControladorTest {
//
//    @Autowired
//    private lateinit var mockMvc: MockMvc
//
//    @MockBean
//    private lateinit var usuarioServicio: UsuarioServicio
//
//    @MockBean
//    private lateinit var userServicio: UserServicio
//
//    @MockBean
//    private lateinit var authoritiesServicio: AuthoritiesServicio
//
//    @MockBean
//    private lateinit var ligaServicio: LigaServicio
//
//    @Test
//    fun testInitCreationForm() {
//        mockMvc.perform(MockMvcRequestBuilders.get("/usuarios/registro"))
//            .andExpect(MockMvcResultMatchers.status().isOk)
//            .andExpect(MockMvcResultMatchers.model().attributeExists("usuario"))
//            .andExpect(MockMvcResultMatchers.view().name("usuarios/registroUsuario"))
//    }
//
//    @Test
//    fun testProcessCreationFormSuccess() {
//        mockMvc.perform(
//            MockMvcRequestBuilders.post("/usuarios/registro")
//                .param("name", "Alejandro")
//                .param("email", "alejandro@gmail.com")
//                .param("user.username", "alex123")
//                .param("user.password", "TFG19alejandro")
//        )
//            .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
//    }
//}

package org.springframework.samples.futgol.liga


import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.usuario.Usuario
import org.springframework.samples.futgol.usuario.UsuarioServicio
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import java.security.Principal
import java.util.stream.Collectors
import javax.validation.Valid


@Controller
class LigaControlador(
    val ligaServicio: LigaServicio,
    val usuarioServicio: UsuarioServicio,
    val equipoServicio: EquipoServicio
) {

    private val VISTA_CREAR_EDITAR_LIGA = "liga/crearEditarLiga"
    private val VISTA_LISTA_LIGAS = "liga/listaLigas"
    private val VISTA_DETALLES_LIGA = "liga/detallesLiga"
    private val VISTA_CLASIFICACION_LIGA = "liga/clasificacion"
    private val VISTA_SUBASTA_LIGA = "liga/subastas"
    private val VISTA_RANKING_USUARIOS = "usuarios/rankingUsuarios"
    private val VISTA_ERROR_403 = "errores/error-403"

    @InitBinder("liga")
    fun initLigaBinder(dataBinder: WebDataBinder) {
        dataBinder.validator = LigaValidador()
    }

    @CachePut("ligas")
    @GetMapping("/misligas")
    fun listaLigas(model: Model, principal: Principal): String {
        val usuario: Usuario? = usuarioServicio.usuarioLogueado(principal)
        val ligas = usuario?.user?.username?.let { usuarioServicio.buscarLigasUsuario(it) }
        if (ligas != null) {
            model["ligas"] = ligas.sortedBy { x -> x.name }
        }
        return VISTA_LISTA_LIGAS
    }

    @GetMapping("/liga/crear")
    fun iniciarCreacion(model: Model, principal: Principal): String {
        val liga = Liga()
        model["liga"] = liga
        return VISTA_CREAR_EDITAR_LIGA
    }

    @PostMapping("/liga/crear")
    fun procesoCreacion(@Valid liga: Liga, result: BindingResult, principal: Principal, model: Model): String {
        if (ligaServicio.comprobarSiExisteLiga(liga.name)) {
            result.addError(FieldError("liga", "name", "Ya existe una liga con ese nombre"))
        }
        return if (result.hasErrors()) {
            model["liga"] = liga
            VISTA_CREAR_EDITAR_LIGA
        } else {
            val usuario: Usuario? = usuarioServicio.usuarioLogueado(principal)
            liga.admin = usuario
            usuario?.ligas?.add(liga)
            if (usuario != null) {
                liga.usuarios.add(usuario)
                this.ligaServicio.guardarLiga(liga)

            }
            "redirect:/liga/" + liga.id + "/nuevoEquipo"
        }
    }

    @GetMapping("/liga/editar/{nombreLiga}")
    fun iniciarActualizacion(
        @PathVariable("nombreLiga") nombreLiga: String, model: Model, principal: Principal
    ): String {
        if (ligaServicio.comprobarSiExisteLiga(nombreLiga)) {
            val liga = ligaServicio.buscarLigaPorNombre(nombreLiga)
            val nombreUsuario = usuarioServicio.usuarioLogueado(principal)?.user?.username
            if (liga?.admin?.user?.username == nombreUsuario) {
                model.addAttribute(liga!!)
            }
        } else {
            return "redirect:/misligas"
        }
        return VISTA_CREAR_EDITAR_LIGA
    }

    @PostMapping("/liga/editar/{nombreLiga}")
    fun procesoActualizacion(
        liga: Liga, principal: Principal, @PathVariable("nombreLiga") nombreLiga: String, result: BindingResult,
        model: Model
    ): String {
        var ligaComparador = ligaServicio.buscarLigaPorNombre(nombreLiga)
        if (liga.name != ligaComparador!!.name && ligaServicio.comprobarSiExisteLiga(liga.name)) {
            result.addError(FieldError("usuario", "name", "Ya existe una liga con ese nombre"))
        }
        return if (result.hasErrors()) {
            VISTA_CREAR_EDITAR_LIGA
        } else {
            liga.id = ligaComparador?.id
            liga.admin = ligaComparador?.admin
            liga.usuariosInvitados = ligaComparador.usuariosInvitados
            liga.usuarios = ligaComparador.usuarios
            this.ligaServicio.guardarLiga(liga)
            "redirect:/misligas"
        }
    }

    @CachePut("detallesLiga")
    @GetMapping("liga/{nombreLiga}")
    fun detallesLiga(model: Model, @PathVariable nombreLiga: String, principal: Principal?): String {
        if (ligaServicio.comprobarSiExisteLiga(nombreLiga) && ligaServicio.estoyEnLiga(nombreLiga, principal)) {
            val liga = ligaServicio.buscarLigaPorNombre(nombreLiga)
            val nombreUsuario = usuarioServicio.usuarioLogueado(principal!!)?.user?.username
            var soyAdmin: Boolean
            var noLimiteEquipos = true
            model["liga"] = liga!!
            var equiposLiga = liga.equipos.sortedBy { x -> x.name }
            model["equipos"] = equiposLiga
            if (liga!!.equipos.size >= 8) {
                noLimiteEquipos = false
            }
            model["noLimiteEquipos"] = noLimiteEquipos

            if (liga.admin?.user?.username.equals(nombreUsuario)) {
                soyAdmin = true
                model["soyAdmin"] = soyAdmin
            }
            var noTengoEquipo: Boolean
            if (liga.id?.let { equipoServicio.tengoEquipo(it, principal) } == false) {
                noTengoEquipo = true
                model["noTengoEquipo"] = noTengoEquipo
            }
            return VISTA_DETALLES_LIGA
        } else {
            return "redirect:/misligas"
        }
    }

    @GetMapping("liga/{nombreLiga}/clasificacion")
    fun clasificacionLiga(model: Model, @PathVariable nombreLiga: String): String {
        var liga = ligaServicio.buscarLigaPorNombre(nombreLiga)
        var equiposLiga = liga?.equipos?.sortedBy { x -> -x.puntos }

        if (equiposLiga != null) {
            var posiciones = equiposLiga.indices
            model["posiciones"] = posiciones
            model["equiposLiga"] = equiposLiga
            var valores: MutableList<Double> = ArrayList()
            for (e in equiposLiga) {
                e.liga?.id?.let { equipoServicio.calcularValorEquipo(e.name, it) }?.let { valores.add(it) }
            }
            model["valores"] = valores
        }
        return VISTA_CLASIFICACION_LIGA
    }

    @GetMapping("topUsuarios")
    fun clasificacionGeneral(model: Model): String {
        var equipos = equipoServicio.buscaTodosEquipos()?.sortedBy { x -> -x.puntos }?.subList(0, 4)
        if (equipos != null) {
            var posiciones = equipos.indices
            model["equipos"] = equipos
            var valores: MutableList<Double> = ArrayList()
            for (e in equipos) {
                e.liga?.id?.let { equipoServicio.calcularValorEquipo(e.name, it) }?.let { valores.add(it) }
            }
            model["valores"] = valores
            model["posiciones"] = posiciones
        }
        return VISTA_RANKING_USUARIOS
    }

    @GetMapping("/liga/{nombreLiga}/add/{username}")
    fun asociarUsuarioLiga(
        @PathVariable username: String,
        @PathVariable nombreLiga: String,
        model: MutableMap<String, Any>
    ): String {

        var usuario = usuarioServicio.buscarUsuarioPorNombreUsuario(username)
        var liga = ligaServicio.buscarLigaPorNombre(nombreLiga)
        if (usuario != null && liga != null) {
            if (!usuario.ligas.contains(liga)) {
                usuario.ligas.add(liga)
                liga.usuarios.add(usuario)
                model["mensaje"] = "Usuario añadido correctamente a la liga!"
            } else {
                model["mensaje"] = "El usuario ya pertenece a la liga"
            }
        }
        return VISTA_DETALLES_LIGA
    }

    @GetMapping("/liga/{idLiga}/subastas")
    fun jugadoresLibresSubasta(@PathVariable idLiga: Int, model: Model): String {
        var jugadoresSinEquipo =
            ligaServicio.buscarJugadoresSinEquipoEnLiga(idLiga).shuffled().stream().limit(15)
                .collect(Collectors.toList())
        model["jugadoresSinEquipo"] = jugadoresSinEquipo
        return VISTA_SUBASTA_LIGA
    }
}

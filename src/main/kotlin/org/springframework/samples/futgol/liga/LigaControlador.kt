package org.springframework.samples.futgol.liga


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

    @GetMapping("/misligas")
    fun listaLigas(model: Model, principal: Principal): String {
        val usuario: Usuario? = usuarioServicio.usuarioLogueado(principal)
        val ligas = usuario?.user?.username?.let { usuarioServicio.buscarLigasUsuario(it) }
        var valores: MutableList<Double> = ArrayList()
        var posiciones: MutableList<Int> = ArrayList()
        if (ligas != null) {
            model["ligas"] = ligas
            for (l in ligas) {
                l.id?.let {
                    if (equipoServicio.tengoEquipo(it, principal)) {
                        var equipo = l.id?.let { equipoServicio.buscaMiEquipoEnLiga(it, principal) }
                        l.id?.let { ligaServicio.calculaPosicionLiga(it, principal)?.let { posiciones.add(it) } }
                        l.id?.let { equipoServicio.calcularValorEquipo(equipo?.name, it) }?.let { valores.add(it) }
                    }
                }
            }
            model["posiciones"] = posiciones
            model["valores"] = valores

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

    @GetMapping("/liga/editar/{idLiga}")
    fun iniciarActualizacion(@PathVariable("idLiga") idLiga: Int, model: Model, principal: Principal): String {
        val liga = this.ligaServicio.buscarLigaPorId(idLiga)
        var adminLiga = liga?.admin?.user?.username
        val usuario = usuarioServicio.usuarioLogueado(principal)?.user?.username
        if (liga != null && usuario != null && adminLiga == usuario) {
            model.addAttribute(liga)
        }
        return VISTA_CREAR_EDITAR_LIGA
    }

    @PostMapping("/liga/editar/{idLiga}")
    fun procesoActualizacion(
        liga: Liga, principal: Principal, @PathVariable("idLiga") idLiga: Int, result: BindingResult,
        model: Model
    ): String {
        var ligaComparador = ligaServicio.buscarLigaPorId(idLiga)
        if (ligaComparador != null) {
            if (liga.name != ligaComparador.name && ligaServicio.comprobarSiExisteLiga(liga.name)) {
                result.addError(FieldError("usuario", "name", "Ya existe una liga con ese nombre"))
            }
        }
        return if (result.hasErrors()) {
            VISTA_CREAR_EDITAR_LIGA
        } else {

            val ligaAntigua = this.ligaServicio.buscarLigaPorId(idLiga)
            if (ligaAntigua != null) {
                liga.id = idLiga
                liga.admin = ligaAntigua?.admin
                liga.usuariosInvitados = ligaAntigua.usuariosInvitados
                liga.usuarios = ligaAntigua.usuarios
            }

            this.ligaServicio.guardarLiga(liga)
            "redirect:/misligas"
        }
    }

    @GetMapping("liga/{nombreLiga}")
    fun detallesLiga(model: Model, @PathVariable nombreLiga: String, principal: Principal): String {
        val liga = ligaServicio.buscarLigaPorNombre(nombreLiga)
        var soyAdmin: Boolean
        val usuario = usuarioServicio.usuarioLogueado(principal)
        val ligas = usuario?.user?.username?.let { usuarioServicio.buscarLigasUsuario(it) }
        var limiteEquipos = true
        if (ligas != null && liga != null) {
            if (ligas.stream().anyMatch { x -> x.name.equals(nombreLiga) }) {
                model["liga"] = liga
                var equiposLiga = liga.equipos
                model["equipos"] = equiposLiga
                if (liga!!.equipos.size >= 8) {
                    limiteEquipos = false
                }
            } else {
                return VISTA_ERROR_403
            }
            model["limiteEquipos"] = limiteEquipos
        }
        if (usuario != null && liga != null) {
            if (liga.admin?.user?.username.equals(usuario.user?.username)) {
                soyAdmin = true
                model["soyAdmin"] = soyAdmin
            }
            var noTengoEquipo = false
            if (liga.id?.let { equipoServicio.tengoEquipo(it, principal) } == false) {
                noTengoEquipo = true
                model["noTengoEquipo"] = noTengoEquipo
            }
        }
        return VISTA_DETALLES_LIGA
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

package org.springframework.samples.futgol.liga


import org.springframework.cache.annotation.CachePut
import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.subasta.Subasta
import org.springframework.samples.futgol.subasta.SubastaServicio
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
import javax.validation.Valid


@Controller
class LigaControlador(
    val ligaServicio: LigaServicio,
    val usuarioServicio: UsuarioServicio,
    val equipoServicio: EquipoServicio,
    val subastaServicio: SubastaServicio
) {

    private val VISTA_CREAR_EDITAR_LIGA = "liga/crearEditarLiga"
    private val VISTA_LISTA_LIGAS = "liga/listaLigas"
    private val VISTA_DETALLES_LIGA = "liga/detallesLiga"
    private val VISTA_CLASIFICACION_LIGA = "liga/clasificacion"

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
        if (ligaServicio.comprobarSiExisteLiga(liga.name) == true) {
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
                this.subastaServicio.subasta(liga.id!!)

            }
            "redirect:/liga/" + liga.id + "/nuevoEquipo"
        }
    }

    @GetMapping("/liga/editar/{nombreLiga}")
    fun iniciarActualizacion(
        @PathVariable("nombreLiga") nombreLiga: String, model: Model, principal: Principal
    ): String {
        if (ligaServicio.comprobarSiExisteLiga(nombreLiga) == true) {
            val liga = ligaServicio.buscarLigaPorNombre(nombreLiga)
            if (liga?.admin?.user?.username == usuarioServicio.usuarioLogueado(principal)?.user?.username) {
                model.addAttribute(liga!!)
            } else {
                return "redirect:/misligas"
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
        val ligaComparador = ligaServicio.buscarLigaPorNombre(nombreLiga)
        if (liga.name != ligaComparador!!.name && ligaServicio.comprobarSiExisteLiga(liga.name) == true) {
            result.addError(FieldError("usuario", "name", "Ya existe una liga con ese nombre"))
        }
        return if (result.hasErrors()) {
            VISTA_CREAR_EDITAR_LIGA
        } else {
            liga.id = ligaComparador.id
            liga.admin = ligaComparador.admin
            liga.usuariosInvitados = ligaComparador.usuariosInvitados
            liga.usuarios = ligaComparador.usuarios
            this.ligaServicio.guardarLiga(liga)
            model["edicionLigaExito"] = true
            listaLigas(model, principal)
        }
    }

    @CachePut("detallesLiga")
    @GetMapping("liga/{nombreLiga}")
    fun detallesLiga(model: Model, @PathVariable nombreLiga: String, principal: Principal?): String {
        if (ligaServicio.comprobarSiExisteLiga(nombreLiga) == true && ligaServicio.estoyEnLiga(nombreLiga, principal)) {
            val liga = ligaServicio.buscarLigaPorNombre(nombreLiga)
            val nombreUsuario = usuarioServicio.usuarioLogueado(principal!!)?.user?.username
            var noLimiteEquipos = true
            model["liga"] = liga!!

            var equiposLiga = liga.equipos.sortedBy { x -> x.name }
            model["equipos"] = equiposLiga
            if (liga.equipos.size >= 8) {
                noLimiteEquipos = false
            }
            model["noLimiteEquipos"] = noLimiteEquipos

            if (liga.admin?.user?.username.equals(nombreUsuario)) {
                model["soyAdmin"] = true
            }
            if (liga.id?.let { equipoServicio.tengoEquipo(it, principal) } == false) {
                model["noTengoEquipo"] = true
            }
            return VISTA_DETALLES_LIGA
        } else {
            return "redirect:/misligas"
        }
    }

    @GetMapping("liga/{nombreLiga}/clasificacion")
    fun clasificacionLiga(model: Model, @PathVariable nombreLiga: String, principal: Principal?): String {
        if (ligaServicio.comprobarSiExisteLiga(nombreLiga) == true && ligaServicio.estoyEnLiga(nombreLiga, principal)) {
            val liga = ligaServicio.buscarLigaPorNombre(nombreLiga)
            model["liga"] = liga!!
            val equiposLiga = liga.equipos.sortedBy { x -> -x.puntos }
            val tam = equiposLiga.size
            if (tam >= 3) {
                model["puedeCalcularse"] = true
                model["numEquipos"] = tam
                model["ganador"] = equiposLiga[0]
                model["segundo"] = equiposLiga[1]
                model["tercero"] = equiposLiga[2]
                model["restoEquipos"] = equiposLiga.drop(3)
            } else {
                model["noPuedeCalcularse"] = true
            }
            return VISTA_CLASIFICACION_LIGA
        }
        return "redirect:/misligas"
    }

}

package org.springframework.samples.futgol.equipo

import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.samples.futgol.jugador.JugadorServicio
import org.springframework.samples.futgol.liga.LigaServicio
import org.springframework.samples.futgol.usuario.UsuarioServicio
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import java.security.Principal
import java.util.stream.Collectors
import javax.validation.Valid

@Controller
class EquipoControlador(
    val ligaServicio: LigaServicio,
    val equipoServicio: EquipoServicio,
    val jugadorServicio: JugadorServicio,
    val usuarioServicio: UsuarioServicio

) {

    private val VISTA_CREAEQUIPOS = "equipos/crearEditarEquipoUsuario"
    private val VISTA_DETALLES_MIEQUIPO = "equipos/detallesMiEquipo"
    private val VISTA_DETALLES_OTROS_EQUIPOS = "equipos/detallesOtroEquipo"


    @GetMapping("/liga/{idLiga}/nuevoEquipo")
    fun iniciarEquipo(model: Model, principal: Principal, @PathVariable idLiga: Int): String {
        return if (equipoServicio.tengoEquipo(idLiga, principal)) {
            var miEquipo = equipoServicio.buscaMiEquipoEnLiga(idLiga, principal)
            model["tengoEquipo"] = true
            model["equipo"] = miEquipo
            "redirect:/liga/$idLiga/miEquipo"
        } else {
            var liga = ligaServicio.buscarLigaPorId(idLiga)
            if (liga!!.equipos.size >= 8) {
                "redirect:/misligas"
            } else {
                val equipo = Equipo()
                model["equipo"] = equipo
                VISTA_CREAEQUIPOS
            }
        }
    }

    @PostMapping("/liga/{idLiga}/nuevoEquipo")
    fun procesoCrearEquipo(
        @Valid equipo: Equipo, result: BindingResult, @PathVariable(("idLiga")) idLiga: Int, principal: Principal,
        model: Model
    ): String {
        var liga = this.ligaServicio.buscarLigaPorId(idLiga)
        if (liga != null) {
            if (liga.id?.let { equipoServicio.comprobarSiExisteEquipoLiga(equipo.name, it) }!!) {
                result.addError(FieldError("equipo", "name", "Ya existe una equipo con ese nombre en esta liga"))
            }
        }
        return if (result.hasErrors()) {
            model["equipo"] = equipo
            VISTA_CREAEQUIPOS
        } else {
            val usuario = usuarioServicio.usuarioLogueado(principal)
            var misJugadores = jugadorServicio.asignarjugadoresNuevoEquipo(idLiga)

            var portero = misJugadores.stream()?.filter { x -> x?.posicion == "PO" }
                .filter { x -> x.estadoLesion == "En forma" }
                .sorted(Comparator.comparing { x -> -x.valor })?.findFirst()?.get()
            var defensas = misJugadores.stream()?.filter { x -> x?.posicion == "DF" }
                .filter { x -> x.estadoLesion == "En forma" }
                .sorted(Comparator.comparing { x -> -x.valor })
                ?.limit(4)
                ?.collect(Collectors.toList())
            var centrocampistas = misJugadores.stream()?.filter { x -> x?.posicion == "CC" }
                .filter { x -> x.estadoLesion == "En forma" }
                .sorted(Comparator.comparing { x -> -x.valor })
                ?.limit(4)
                ?.collect(Collectors.toList())
            var delanteros = misJugadores.stream()?.filter { x -> x?.posicion == "DL" }
                .filter { x -> x.estadoLesion == "En forma" }
                .sorted(Comparator.comparing { x -> -x.valor })
                ?.limit(2)
                ?.collect(Collectors.toList())
            var onceInicial = HashSet<Jugador>()
            if (portero != null) {
                onceInicial.add(portero)
            }
            if (defensas != null) {
                onceInicial.addAll(defensas)
            }
            if (centrocampistas != null) {
                onceInicial.addAll(centrocampistas)
            }
            if (delanteros != null) {
                onceInicial.addAll(delanteros)
            }

            var banquillo= HashSet<Jugador>(misJugadores)
            banquillo.removeAll(onceInicial)

            equipo.onceInicial = onceInicial
            equipo.jugBanquillo = banquillo
            equipo.jugadores = misJugadores
            equipo.usuario = usuario
            equipo.dineroRestante = 25000000
            equipo.liga = liga
            equipo.formacion = "4-4-2"

            this.equipoServicio.guardarEquipo(equipo)
            "redirect:/liga/$idLiga/miEquipo"
        }
    }

    @GetMapping("liga/{idLiga}/miEquipo")
    fun detallesMiEquipo(
        model: Model, @PathVariable("idLiga") idLiga: Int, principal: Principal
    ): String {
        if (!equipoServicio.tengoEquipo(idLiga, principal)) {
            model["liga"] = ligaServicio.buscarLigaPorId(idLiga)!!
            model["SinEquipo"] = true
        } else {
            var miEquipo = equipoServicio.buscaMiEquipoEnLiga(idLiga, principal)

            model["tengoEquipo"] = true
            model["equipo"] = miEquipo
            model["banquillo"] = miEquipo.jugBanquillo

            model["formacion"]= miEquipo.formacion

            model["valorEquipo"] = equipoServicio.calcularValorEquipo(miEquipo.name, idLiga)!!
            model["liga"] = ligaServicio.buscarLigaPorId(idLiga)!!
            //CAMBIAR A ONCE INICIAL CUANDO ESTÉ PUESTO Y PONER TB LOS JUGADORES DEL BANQUILLO
            var portero = miEquipo.onceInicial.stream()?.filter { x -> x?.posicion == "PO" }
                .filter { x -> x.estadoLesion == "En forma" }
                .sorted(Comparator.comparing { x -> -x.valor })?.findFirst()?.get()
            model["portero"] = portero!!
            var defensas = miEquipo.onceInicial.stream()?.filter { x -> x?.posicion == "DF" }
                .filter { x -> x.estadoLesion == "En forma" }
                .sorted(Comparator.comparing { x -> -x.valor })
                ?.limit(4)
                ?.collect(Collectors.toList())
            model["defensas"] = defensas!!
            var centrocampistas = miEquipo.onceInicial.stream()?.filter { x -> x?.posicion == "CC" }
                .filter { x -> x.estadoLesion == "En forma" }
                .sorted(Comparator.comparing { x -> -x.valor })
                ?.limit(4)
                ?.collect(Collectors.toList())
            model["centrocampistas"] = centrocampistas!!
            var delanteros = miEquipo.onceInicial.stream()?.filter { x -> x?.posicion == "DL" }
                .filter { x -> x.estadoLesion == "En forma" }
                .sorted(Comparator.comparing { x -> -x.valor })
                ?.limit(2)
                ?.collect(Collectors.toList())
            model["delanteros"] = delanteros!!
        }
        return VISTA_DETALLES_MIEQUIPO
    }

    @GetMapping("liga/{idLiga}/miEquipo/cambiarFormacion/{formacion}")
    fun cambiarFormacion(
        model: Model, principal: Principal, @PathVariable idLiga: Int, @PathVariable formacion: String
    ): String {
        if (equipoServicio.tengoEquipo(idLiga, principal)) {
            var miEquipo = equipoServicio.buscaMiEquipoEnLiga(idLiga, principal)
            var formacionAntigua= miEquipo.formacion
            if (formacion == "442" && formacionAntigua != "4-4-2") {
                miEquipo.formacion = "4-4-2"
            } else if (formacion == "433" && formacionAntigua != "4-3-3") {
                miEquipo.formacion = "4-3-3"
            } else if (formacion == "352" && formacionAntigua != "3-5-2") {
                miEquipo.formacion = "3-5-2"
            }else if(formacion == "532" && formacionAntigua != "5-3-2"){
                miEquipo.formacion = "5-3-2"
            }
            if(formacionAntigua!=miEquipo.formacion){
                this.equipoServicio.guardarEquipo(miEquipo)
            }
        }
        return "redirect:/liga/$idLiga/miEquipo"
    }

    @GetMapping("liga/{nombreLiga}/equipos/{idEquipo}")
    fun detallesEquipo(
        model: Model, @PathVariable("nombreLiga") nombreLiga: String,
        principal: Principal,
        @PathVariable("idEquipo") idEquipo: Int
    ): String {
        var liga = ligaServicio.buscarLigaPorNombre(nombreLiga)!!
        model["liga"] = liga
        var equipo = equipoServicio.buscaEquiposPorId(idEquipo)!!
        model["equipo"] = equipo
        var jugadores = equipo.jugadores
        model["jugadores"] = jugadores
        model["valorEquipo"] = liga.id?.let { equipoServicio.calcularValorEquipo(equipo.name, it) }!!
        if (jugadores.size > 5) {
            model["top5Jugadores"] = liga.id?.let { equipoServicio.topJugadoresEquipo(equipo.name, it) }!!
        }
        return if (equipo.name != liga.id?.let { equipoServicio.buscaMiEquipoEnLiga(it, principal).name }) {
            VISTA_DETALLES_OTROS_EQUIPOS
        } else {
            "redirect:/liga/" + liga.id + "/miEquipo"
        }
    }

    @GetMapping("liga/{idLiga}/miEquipo/once/add/{idJugador}")
    fun meterOnceInicial(
        @PathVariable idLiga: Int, @PathVariable idJugador: Int,
        model: Model, principal: Principal
    ): String {
        var jugador = jugadorServicio.buscaJugadorPorId(idJugador)
        var equipo = equipoServicio.buscaMiEquipoEnLiga(idLiga, principal)
        if (jugador != null) {
            for (j in equipo.jugBanquillo) {
                if (jugador.id == j.id) {
                    equipo.jugBanquillo.removeIf { it.name == jugador.name }
                    equipo.onceInicial.add(jugador)
                    equipoServicio.guardarEquipo(equipo)
                    break
                }
            }
        }
        return "redirect:/liga/$idLiga/miEquipo"
    }

    @GetMapping("liga/{idLiga}/miEquipo/once/remove/{idJugador}")
    fun retirarOnceInicial(
        @PathVariable idLiga: Int, @PathVariable idJugador: Int,
        model: Model, principal: Principal
    ): String {

        var jugador = jugadorServicio.buscaJugadorPorId(idJugador)
        var equipo = equipoServicio.buscaMiEquipoEnLiga(idLiga, principal)
        if (jugador != null) {
            for (j in equipo.onceInicial) {
                if (jugador.id == j.id) {
                    equipo.jugBanquillo.add(jugador)
                    equipo.onceInicial.removeIf { it.name == jugador.name }
                    equipoServicio.guardarEquipo(equipo)
                    break
                }
            }
        }
        return "redirect:/liga/$idLiga/miEquipo"
    }

}

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
import kotlin.math.absoluteValue

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
    private val VISTA_GESTION_ONCE = "equipos/gestionOnce"


    @GetMapping("/liga/{idLiga}/nuevoEquipo")
    fun iniciarEquipo(model: Model, principal: Principal, @PathVariable idLiga: Int): String {
        return if (equipoServicio.tengoEquipo(idLiga, principal)) {
            val miEquipo = equipoServicio.buscaMiEquipoEnLiga(idLiga, principal)
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
        @Valid equipo: Equipo, result: BindingResult, @PathVariable("idLiga") idLiga: Int, principal: Principal,
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


            var portero = misJugadores.stream().filter { x -> x?.posicion == "PO" }
                .filter { x -> x.estadoLesion == "En forma" }
                .sorted(Comparator.comparing { x -> -x.valor })?.findFirst()?.get()
            var defensas = misJugadores.stream().filter { x -> x?.posicion == "DF" }
                .filter { x -> x.estadoLesion == "En forma" }
                .sorted(Comparator.comparing { x -> -x.valor })
                ?.limit(4)
                ?.collect(Collectors.toList())
            var centrocampistas = misJugadores.stream().filter { x -> x?.posicion == "CC" }
                .filter { x -> x.estadoLesion == "En forma" }
                .sorted(Comparator.comparing { x -> -x.valor })
                ?.limit(4)
                ?.collect(Collectors.toList())
            var delanteros = misJugadores.stream().filter { x -> x?.posicion == "DL" }
                .filter { x -> x.estadoLesion == "En forma" }
                .sorted(Comparator.comparing { x -> -x.valor })
                .limit(2)
                .collect(Collectors.toList())

            var onceInicial = HashSet<Jugador>()

            onceInicial.add(portero)
            onceInicial.addAll(defensas)
            onceInicial.addAll(centrocampistas)
            onceInicial.addAll(delanteros)

            var banquillo = HashSet<Jugador>(misJugadores)
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
            var NPortero = miEquipo.onceInicial.stream().filter { x -> x.posicion == "PO" }
                .findFirst().get()
            var NDefensas = miEquipo.onceInicial.stream().filter { x -> x.posicion == "DF" }
                .collect(Collectors.toList())
            var NCentrocampistas = miEquipo.onceInicial.stream().filter { x -> x.posicion == "CC" }
                .collect(Collectors.toList())
            var NDelanteros = miEquipo.onceInicial.stream().filter { x -> x.posicion == "DL" }
                .collect(Collectors.toList())


            var onceInicial = ArrayList<Jugador>()

            onceInicial.add(NPortero)
            onceInicial.addAll(NDefensas)
            onceInicial.addAll(NCentrocampistas)
            onceInicial.addAll(NDelanteros)

            miEquipo.onceInicial = onceInicial.toSet() as MutableSet<Jugador>
            this.equipoServicio.guardarEquipo(miEquipo)
            model["tengoEquipo"] = true
            model["equipo"] = miEquipo
            model["valorEquipo"] = equipoServicio.calcularValorEquipo(miEquipo.name, idLiga)
            model["liga"] = ligaServicio.buscarLigaPorId(idLiga)!!

        }
        return VISTA_DETALLES_MIEQUIPO
    }

    @GetMapping("liga/{idLiga}/miEquipo/cambiarFormacion/{formacion}")
    fun cambiarFormacion(
        model: Model, principal: Principal, @PathVariable idLiga: Int, @PathVariable formacion: String
    ): String {
        if (equipoServicio.tengoEquipo(idLiga, principal)) {
            var miEquipo = equipoServicio.buscaMiEquipoEnLiga(idLiga, principal)
            var formacionAntigua = miEquipo.formacion
            var onceAntiguo = miEquipo.onceInicial
            var banqAntiguo = miEquipo.jugBanquillo

            var defensasOnce =
                onceAntiguo.stream().filter { x -> x.posicion == "DF" }.collect(Collectors.toList())
            var centroCampistasOnce =
                onceAntiguo.stream().filter { x -> x.posicion == "CC" }.collect(Collectors.toList())
            var delanterosOnce =
                onceAntiguo.stream().filter { x -> x.posicion == "DL" }.collect(Collectors.toList())
            var defensasBanq =
                banqAntiguo.stream().filter { x -> x.posicion == "DF" }.collect(Collectors.toList())
            var centroCampistasBanq =
                banqAntiguo.stream().filter { x -> x.posicion == "CC" }.collect(Collectors.toList())
            var delanterossBanq =
                banqAntiguo.stream().filter { x -> x.posicion == "DL" }.collect(Collectors.toList())

            var nuevoOnce: MutableSet<Jugador> = onceAntiguo
            var nuevoBanq: MutableSet<Jugador> = banqAntiguo

            var nDFBQ = defensasBanq.size
            var nCCBQ = centroCampistasBanq.size
            var nDLBQ = delanterossBanq.size

            var DFRestantes = 0
            var CCRestantes = 0
            var DLRestantes = 0

            if (formacion == "442" && formacionAntigua != "4-4-2") {
                if (formacionAntigua == "4-3-3") {
                    if (nCCBQ >= 1) {
                        CCRestantes = 1
                        DLRestantes = -1

                    }
                } else if (formacionAntigua == "3-5-2") {
                    if (nDFBQ >= 1) {
                        DFRestantes = 1
                        CCRestantes = -1
                    }
                } else {
                    if (nCCBQ >= 1) {
                        DFRestantes = -1
                        CCRestantes = 1
                    }
                }
                miEquipo.formacion = "4-4-2"

            } else if (formacion == "433" && formacionAntigua != "4-3-3") {
                if (formacionAntigua == "4-4-2") {
                    if (nDLBQ >= 1) {
                        CCRestantes = -1
                        DLRestantes = 1
                    }
                } else if (formacionAntigua == "3-5-2") {
                    if (nDFBQ >= 1 && nDLBQ >= 1) {
                        DFRestantes = 1
                        CCRestantes = -2
                        DLRestantes = 1
                    }
                } else {
                    DFRestantes = -1
                    DLRestantes = 1
                }
                miEquipo.formacion = "4-3-3"
            } else if (formacion == "352" && formacionAntigua != "3-5-2") {
                if (formacionAntigua == "4-4-2") {
                    if (nCCBQ >= 1) {
                        DFRestantes = -1
                        CCRestantes = 1
                    }
                } else if (formacionAntigua == "4-3-3") {
                    if (nCCBQ >= 2) {
                        DFRestantes = -1
                        CCRestantes = 2
                        DLRestantes = -1
                    }
                } else {
                    if (nCCBQ >= 2) {
                        DFRestantes = -2
                        CCRestantes = 2
                    }
                }
                miEquipo.formacion = "3-5-2"
            } else if (formacion == "532" && formacionAntigua != "5-3-2") {
                if (formacionAntigua == "4-4-2") {
                    if (nDFBQ >= 1) {
                        DFRestantes = 1
                        CCRestantes = -1
                    }
                } else if (formacionAntigua == "4-3-3") {
                    if (nDFBQ >= 1) {
                        DFRestantes = 1
                        DLRestantes = -1
                    }
                } else {
                    if (nDFBQ >= 2) {
                        DFRestantes = 2
                        CCRestantes = -2
                    }
                }
                miEquipo.formacion = "5-3-2"
            }
            if (formacionAntigua != miEquipo.formacion) {
                if (DFRestantes > 0) {
                    for (n in 0 until DFRestantes) {
                        nuevoBanq.remove(defensasBanq[n])
                        nuevoOnce.add(defensasBanq[n])
                        defensasBanq.removeAt(n)
                    }
                } else if (DFRestantes < 0) {
                    for (n in 0 until DFRestantes.absoluteValue) {
                        nuevoBanq.add(defensasOnce[n])
                        nuevoOnce.remove(defensasOnce[n])
                        defensasOnce.removeAt(n)
                    }
                }

                if (CCRestantes > 0) {
                    for (n in 0 until CCRestantes) {
                        nuevoBanq.remove(centroCampistasBanq[n])
                        nuevoOnce.add(centroCampistasBanq[n])
                        centroCampistasBanq.removeAt(n)
                    }
                } else if (CCRestantes < 0) {
                    for (n in 0 until CCRestantes.absoluteValue) {
                        nuevoBanq.add(centroCampistasOnce[n])
                        nuevoOnce.remove(centroCampistasOnce[n])
                        centroCampistasOnce.removeAt(n)
                    }
                }

                if (DLRestantes > 0) {
                    for (n in 0 until DLRestantes) {
                        nuevoBanq.remove(delanterossBanq[n])
                        nuevoOnce.add(delanterossBanq[n])
                        delanterossBanq.removeAt(n)
                    }
                } else if (DLRestantes < 0) {
                    for (n in 0 until DLRestantes.absoluteValue) {
                        nuevoBanq.add(delanterosOnce[n])
                        nuevoOnce.remove(delanterosOnce[n])
                        delanterosOnce.removeAt(n)
                    }
                }

                miEquipo.onceInicial = nuevoOnce
                miEquipo.jugBanquillo = nuevoBanq

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

    @GetMapping("liga/{idLiga}/miEquipo/cambiarOnce/{idJugador}")
    fun sustitutosPosibles(
        @PathVariable idLiga: Int, @PathVariable idJugador: Int,
        model: Model, principal: Principal
    ): String {

        var equipo = equipoServicio.buscaMiEquipoEnLiga(idLiga, principal)
        model["equipo"] = equipo
        if (jugadorServicio.existeJugadorId(idJugador) == true && equipo.id?.let {
                jugadorServicio.checkJugadorEnEquipo(idJugador, it)
            } == true &&
            equipo.id?.let { equipoServicio.jugadorEnOnce(idJugador, it) } == true) {
            model["liga"] = ligaServicio.buscarLigaPorId(idLiga)!!
            model["titular"] = jugadorServicio.buscaJugadorPorId(idJugador)!!
            var jugadoresMismaPos =
                equipo.id?.let { equipoServicio.buscaJugadoresBanqEquipoMismaPos(it, idJugador) }
            model["jugadoresMismaPos"] = jugadoresMismaPos!!
        } else {
            return "redirect:/liga/" + idLiga + "/miEquipo"
        }
        return VISTA_GESTION_ONCE
    }

    @GetMapping("liga/{idLiga}/miEquipo/cambiarOnce/{idJugadorOnce}/por/{idJugadorBanquillo}")
    fun sustituirJugadoresOnce(
        @PathVariable idLiga: Int, @PathVariable idJugadorOnce: Int, @PathVariable idJugadorBanquillo: Int,
        model: Model, principal: Principal
    ): String {

        var equipo = equipoServicio.buscaMiEquipoEnLiga(idLiga, principal)
        if (jugadorServicio.existeJugadorId(idJugadorOnce) == true && equipo.id?.let {
                jugadorServicio.checkJugadorEnEquipo(idJugadorOnce, it)
            } == true &&
            equipo.id?.let { equipoServicio.jugadorEnOnce(idJugadorOnce, it) } == true) {
            if (jugadorServicio.existeJugadorId(idJugadorBanquillo) == true && equipo.id?.let {
                    jugadorServicio.checkJugadorEnEquipo(idJugadorBanquillo, it)
                } == true &&
                equipo.id?.let {
                    equipoServicio.jugadorEnOnce(
                        idJugadorBanquillo,
                        it
                    )
                } == false && jugadorServicio.tienenMismaPos(idJugadorOnce, idJugadorBanquillo)!!) {

                model["liga"] = ligaServicio.buscarLigaPorId(idLiga)!!
                var titular = jugadorServicio.buscaJugadorPorId(idJugadorOnce)!!
                var sustituto = jugadorServicio.buscaJugadorPorId(idJugadorBanquillo)!!
                var equipo = equipoServicio.buscaMiEquipoEnLiga(idLiga, principal)
                model["equipo"] = equipo

                for (jug in equipo.jugBanquillo) {
                    if (jug.id == sustituto.id) {
                        equipo.jugBanquillo.remove(jug)
                        break
                    }
                }
                for (jug in equipo.onceInicial) {
                    if (jug.id == titular.id) {
                        equipo.onceInicial.remove(jug)
                        break
                    }
                }
                equipo.jugBanquillo.add(titular)
                equipo.onceInicial.add(sustituto)
                equipoServicio.guardarEquipo(equipo)
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

package org.springframework.samples.futgol.equipo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.jornadas.JornadaServicio
import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.samples.futgol.jugador.JugadorServicio
import org.springframework.samples.futgol.liga.LigaServicio
import org.springframework.samples.futgol.movimientos.Movimiento
import org.springframework.samples.futgol.movimientos.MovimientoServicio
import org.springframework.samples.futgol.partido.Partido
import org.springframework.samples.futgol.puntosJornadaEquipo.PtosJornadaEquipo
import org.springframework.samples.futgol.puntosJornadaEquipo.PtosJornadaEquipoServicio
import org.springframework.samples.futgol.usuario.UsuarioServicio
import org.springframework.samples.futgol.util.MetodosAux
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.Principal

@Service
class EquipoServicio {

    private var equipoRepositorio: EquipoRepositorio? = null

    @Autowired
    fun EquipoServicio(equipoRepositorio: EquipoRepositorio) {
        this.equipoRepositorio = equipoRepositorio
    }

    @Autowired
    private var usuarioServicio: UsuarioServicio? = null

    @Autowired
    private var jugadorServicio: JugadorServicio? = null

    @Autowired
    private var ptosJornadaEquipoServicio: PtosJornadaEquipoServicio? = null

    @Autowired
    private var jornadaServicio: JornadaServicio? = null

    @Autowired
    private var ligaServicio: LigaServicio? = null

    @Autowired
    private var movimientoServicio: MovimientoServicio? = null


    @Transactional
    @Throws(DataAccessException::class)
    fun guardarEquipo(equipo: Equipo) {
        equipoRepositorio?.save(equipo)
    }

    @Transactional(readOnly = true)
    fun buscaEquiposDeLigaPorId(idLiga: Int): Collection<Equipo>? {
        return equipoRepositorio?.buscarEquiposDeLigaPorId((idLiga))
    }

    @Transactional(readOnly = true)
    fun jugadorEnOnce(idJugador: Int, idEquipo: Int): Boolean {
        var equipo = this.buscaEquiposPorId(idEquipo)
        return equipo?.onceInicial?.any { x -> x.id == idJugador }!!
    }

    @Transactional(readOnly = true)
    fun buscaJugadoresBanqEquipoMismaPos(
        idEquipo: Int, idJugador: Int
    ): MutableList<Jugador>? {
        var equipo = buscaEquiposPorId(idEquipo)
        var posicion = jugadorServicio?.buscaJugadorPorId(idJugador)?.posicion
        return equipo?.jugBanquillo?.filter { x -> x.posicion == posicion } as MutableList<Jugador>
    }

    @Transactional(readOnly = true)
    fun estaJugadorEnEquiposLiga(idLiga: Int, idJugador: Int): Boolean {
        val equipos = buscaEquiposDeLigaPorId(idLiga)!!
        val jugador = jugadorServicio!!.buscaJugadorPorId(idJugador)!!
        for (e in equipos) {
            if (e.jugadores.contains(jugador)) return true
        }
        return false
    }

    @Transactional(readOnly = true)
    fun buscarEquipoLigaJugador(idLiga: Int, idJugador: Int): Equipo {
        val equipos = buscaEquiposDeLigaPorId(idLiga)!!
        val jugador = jugadorServicio!!.buscaJugadorPorId(idJugador)!!
        for (e in equipos) {
            if (e.jugadores.contains(jugador)) return e
        }
        return Equipo()
    }


    @Transactional(readOnly = true)
    fun buscaEquiposPorId(idEquipo: Int): Equipo? {
        return equipoRepositorio?.buscaEquiposPorId(idEquipo)
    }

    fun queFormacionMeVale(numDF: Int, numCC: Int, numDL: Int): String {
        if (numDF >= 4 && numCC >= 4 && numDL >= 2) {
            return "4-4-2"
        } else if (numDF >= 4 && numCC >= 3 && numDL >= 3) {
            return "4-3-3"
        } else if (numDF >= 4 && numCC >= 5 && numDL >= 1) {
            return "4-5-1"
        } else if (numDF >= 5 && numCC >= 3 && numDL >= 2) {
            return "5-3-2"
        } else if (numDF >= 3 && numCC >= 5 && numDL >= 2) {
            return "3-5-2"
        }
        return "4-4-2"
    }

    @Transactional
    fun cambiaFormacion(formacion: String, equipo: Equipo) {
        val once = equipo.onceInicial
        val numDF = once.filter { x -> x.posicion == "DF" }.size
        val numCC = once.filter { x -> x.posicion == "CC" }.size
        val numDL = once.filter { x -> x.posicion == "DL" }.size
        equipo.formacion = queFormacionMeVale(numDF, numCC, numDL)
        guardarEquipo(equipo)
    }

    @Transactional
    fun ajustaOnce(formacionAntigua: String, formacionNueva: String, equipo: Equipo) {

    }

    @Transactional
    fun compruebaBuenaFormacion(formacion: String, equipo: Equipo): Boolean? {
        val once = equipo?.onceInicial!!
        val numDF = once.filter { x -> x.posicion == "DF" }.size
        val numCC = once.filter { x -> x.posicion == "CC" }.size
        val numDL = once.filter { x -> x.posicion == "DL" }.size
        when (formacion) {
            "4-4-2" -> return numDF >= 4 && numCC >= 4 && numDL >= 2
            "4-5-1" -> return numDF >= 4 && numCC >= 5 && numDL >= 1
            "4-3-3" -> return numDF >= 4 && numCC >= 3 && numDL >= 3
            "3-5-2" -> return numDF >= 3 && numCC >= 5 && numDL >= 2
            "5-3-2" -> return numDF >= 5 && numCC >= 3 && numDL >= 2
        }
        return true
    }

    @Transactional(readOnly = true)
    fun puedoVenderJugador(idEquipo: Int, idJugador: Int): Boolean? {
        val banq = buscaEquiposPorId(idEquipo)?.jugBanquillo!!
        val posicionJug = jugadorServicio?.buscaJugadorPorId(idJugador)?.posicion
        if (posicionJug == "DF") {
            return banq.filter { x -> x.posicion == "DF" }.isNotEmpty()
        }
        if (posicionJug == "PO") {
            return banq.filter { x -> x.posicion == "PO" }.isNotEmpty()
        }
        if (posicionJug == "CC") {
            return banq.filter { x -> x.posicion == "CC" }.isNotEmpty()
        }
        if (posicionJug == "DL") {
            return banq.filter { x -> x.posicion == "Dl" }.isNotEmpty()
        }
        return true
    }

    @Transactional(readOnly = true)
    fun buscaTodosEquipos(): Collection<Equipo>? {
        return equipoRepositorio?.findAll()
    }

    @Transactional(readOnly = true)
    fun buscaMiEquipoEnLiga(idLiga: Int, principal: Principal): Equipo {
        var nombreUsuario = usuarioServicio?.usuarioLogueado(principal)?.user?.username
        return nombreUsuario?.let { this.equipoRepositorio?.buscarEquipoUsuarioEnLiga(it, idLiga) }!!
    }

    @Transactional(readOnly = true)
    fun tengoEquipo(idLiga: Int, principal: Principal): Boolean {
        var nombreUsuario = usuarioServicio?.usuarioLogueado(principal)?.user?.username
        return nombreUsuario?.let { this.equipoRepositorio?.existeUsuarioConEquipoEnLiga(it, idLiga) }!!
    }

    @Transactional(readOnly = true)
    fun comprobarSiExisteEquipoLiga(nombreEquipo: String?, idLiga: Int): Boolean {
        return nombreEquipo?.let { this.equipoRepositorio?.existeEquipoEnLiga(it, idLiga) }!!
    }

    @Transactional(readOnly = true)
    fun comprobarSiExisteEquipo(idEquipo: Int): Boolean? {
        return equipoRepositorio?.existeEquipo(idEquipo)
    }

    @Transactional(readOnly = true)
    fun tengoEquipoLiga(idLiga: Int, idUsuario: Int): Boolean? {
        return equipoRepositorio?.tengoEquipoLiga(idLiga, idUsuario)
    }

    @Transactional(readOnly = true)
    fun buscarEquipoPorNombreYLiga(nombreEquipo: String, idLiga: Int): Equipo? {
        return equipoRepositorio?.buscarEquipoPorNombreYLiga(nombreEquipo, idLiga)
    }

    @Transactional(readOnly = true)
    fun buscarEquipoPorJugadorYLiga(nombreJugador: String, idLiga: Int): Equipo? {
        return equipoRepositorio?.buscarEquiposDeLigaPorId(idLiga)
            ?.filter { x -> x.jugadores.any { j -> j.name == nombreJugador } }
            ?.get(0)
    }

    @Transactional
    fun asignaPuntosDineroEquipo(nombreEquipo: String, idLiga: Int) {
        if (comprobarSiExisteEquipoLiga(nombreEquipo, idLiga)) {
            var equipo = buscarEquipoPorNombreYLiga(nombreEquipo, idLiga)!!
            var jornada = this.jornadaServicio?.buscarTodasJornadas()?.stream()
                ?.filter { x -> x.partidos.stream().allMatch { p -> p.resultado == "" } }
                ?.min(Comparator.comparing { x -> x.numeroJornada })?.get()
            var ptoJEq = PtosJornadaEquipo()
            ptoJEq.equipo = equipo
            if (jornada != null) {
                var jornadaBuena = jornadaServicio?.buscarJornadaPorNumeroJornada(jornada.numeroJornada - 1)
                ptoJEq.jornada = jornadaBuena
                for (p in jornada.partidos) {
                    auxAsignaPuntosEquipo(p, ptoJEq)
                }
            }
            ptosJornadaEquipoServicio?.guardarPtosJornadaEquipo(ptoJEq)
            equipo.puntos += ptoJEq.puntos
            val dineroJEq = ptoJEq.puntos * 400000
            equipo.dineroRestante += dineroJEq
            val movimiento = Movimiento()
            movimiento.creadorMovimiento = equipo.usuario
            movimiento.liga = this.ligaServicio?.buscarLigaPorId(idLiga)
            movimiento.jugador = null
            movimiento.jugador2 = null
            movimiento.creadorMovimiento2 = null
            movimiento.texto =
                equipo.usuario?.user?.username + " ha ganado " + MetodosAux().enteroAEuros(
                    dineroJEq
                ) + " por puntos en la jornada " + ptoJEq.jornada?.numeroJornada + "."
            movimiento.textoPropio =
                "Has ganado " + MetodosAux().enteroAEuros(
                    dineroJEq
                ) + " por puntos en la jornada " + ptoJEq.jornada?.numeroJornada + "."
            this.guardarEquipo(equipo)
            this.movimientoServicio?.guardarMovimiento(movimiento)
        }
    }

    fun asignarPuntosEquipo() {
        var ligas = this.ligaServicio?.buscarTodasLigas()!!
        for (l in ligas) {
            var equipos = l.id?.let { this.buscaEquiposDeLigaPorId(it) }
            if (equipos != null) {
                for (e in equipos) {
                    e.name?.let { l.id?.let { it1 -> this.asignaPuntosDineroEquipo(it, it1) } }
                }
            }
        }
    }

    @Transactional(readOnly = true)
    fun auxAsignaPuntosEquipo(p: Partido, ptoJEq: PtosJornadaEquipo) {
        for (j in p.equipoLocal!!.jugadores) {
            if (j in ptoJEq.equipo!!.jugadores) {
                for (est in j.estadisticas) {
                    if (est.partido?.jornada?.numeroJornada == ptoJEq.jornada?.numeroJornada) {
                        ptoJEq.puntos += est.puntos
                    }
                }
            }
        }
        for (j in p.equipoVisitante!!.jugadores) {
            if (j in ptoJEq.equipo!!.jugadores) {
                for (est in j.estadisticas) {
                    if (est.partido?.jornada?.numeroJornada == ptoJEq.jornada?.numeroJornada) {
                        ptoJEq.puntos += est.puntos
                    }
                }
            }
        }
    }

    @Transactional(readOnly = true)
    fun calcularValorEquipo(nombreEquipo: String, idLiga: Int): Double {
        var res = 0.0
        if (this.comprobarSiExisteEquipoLiga(nombreEquipo, idLiga)) {
            var valorEquipo = 0.0
            var equipo = this.buscarEquipoPorNombreYLiga(nombreEquipo, idLiga)
            for (j in equipo?.jugadores!!) {
                valorEquipo += j.valor
            }
            res = Math.round(valorEquipo * 100) / 100.0
        }
        return res
    }

    @Transactional(readOnly = true)
    fun buscaEquiposEnListaEquipos(equipos: MutableSet<Equipo>, nombreEquipo: String?): Equipo {
        val equipo = Equipo()
        for (e in equipos) {
            if (e.name == nombreEquipo) {
                return e
            }
        }
        return equipo
    }

    @Transactional(readOnly = true)
    fun topJugadoresEquipo(nombreEquipo: String?, idLiga: Int): List<Jugador>? {
        val equipos = ligaServicio?.buscarLigaPorId(idLiga)?.equipos
        val equipo = equipos?.let { buscaEquiposEnListaEquipos(it, nombreEquipo) }
        return equipo?.jugadores?.sortedBy { j -> -j.puntos }?.subList(0, 4)

    }
}

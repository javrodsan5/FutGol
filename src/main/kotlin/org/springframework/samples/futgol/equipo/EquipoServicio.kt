package org.springframework.samples.futgol.equipo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.samples.futgol.jugador.JugadorServicio
import org.springframework.samples.futgol.liga.LigaServicio
import org.springframework.samples.futgol.usuario.UsuarioServicio
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils
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
    private var ligaServicio: LigaServicio? = null


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
    fun buscaEquiposPorId(idEquipo: Int): Equipo? {
        return equipoRepositorio?.buscaEquiposPorId(idEquipo)
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
    fun buscarEquipoPorNombreYLiga(nombreEquipo: String, idLiga: Int): Equipo {
        return this.buscarEquipoPorNombreYLiga(nombreEquipo, idLiga)
    }

    @Transactional(readOnly = true)
    fun calcularValorEquipo(nombreEquipo: String, idLiga: Int): Double {
        var res = 0.0
        if (this.comprobarSiExisteEquipoLiga(nombreEquipo, idLiga)) {
            var valorEquipo = 0.0
            var equipo = this.buscarEquipoPorNombreYLiga(nombreEquipo, idLiga)
            for (j in equipo.jugadores) {
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

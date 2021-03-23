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
        var res = false
        var equipo = buscaEquiposPorId(idEquipo)
        for (jug in equipo!!.onceInicial) {
            if (jug.id == idJugador) {
                res = true
                break
            }
        }
        return res
    }

    @Transactional(readOnly = true)
    fun buscaJugadoresBanqEquipoMismaPos(
        idEquipo: Int, idJugador: Int
    ): MutableList<Jugador>? {
        var listaSustitutos: MutableList<Jugador> = ArrayList()
        var equipo = buscaEquiposPorId(idEquipo)
        var posicion = jugadorServicio?.buscaJugadorPorId(idJugador)?.posicion
        for (jug in equipo!!.jugBanquillo) {
            if (jug.posicion == posicion) {
                listaSustitutos.add(jug)
            }
        }
        return listaSustitutos
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
        return nombreUsuario?.let { this.equipoRepositorio?.buscarEquipoUsuarioEnLiga(it,idLiga) }!!
    }

    @Transactional(readOnly = true)
    fun tengoEquipo(idLiga: Int, principal: Principal): Boolean {
        var nombreUsuario = usuarioServicio?.usuarioLogueado(principal)?.user?.username
        return nombreUsuario?.let { this.equipoRepositorio?.existeUsuarioConEquipoEnLiga(it,idLiga) }!!
    }

    @Transactional(readOnly = true)
    fun comprobarSiExisteEquipoLiga(nombreEquipo: String?, idLiga: Int): Boolean {
        return nombreEquipo?.let { this.equipoRepositorio?.existeEquipoEnLiga(it,idLiga) }!!
    }

    @Transactional(readOnly = true)
    fun calcularValorEquipo(nombreEquipo: String?, idLiga: Int): Double {
        val equipos = buscaEquiposDeLigaPorId(idLiga)
        var valorEquipo = 0.0
        var equipo = Equipo()
        if (equipos != null) {
            for (e in equipos) {
                if (e.name == nombreEquipo) {
                    equipo = e
                    break
                }
            }
            for (j in equipo.jugadores) {
                valorEquipo += j.valor
            }
        }
        return Math.round(valorEquipo * 100) / 100.0
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

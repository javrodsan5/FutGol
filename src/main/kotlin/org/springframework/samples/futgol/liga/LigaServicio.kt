package org.springframework.samples.futgol.liga

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.samples.futgol.jugador.JugadorServicio
import org.springframework.samples.futgol.usuario.UsuarioServicio
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.Principal
import java.util.*
import java.util.stream.Collectors

@Service
class LigaServicio {

    private var ligaRepositorio: LigaRepositorio? = null

    @Autowired
    private val jugadorServicio: JugadorServicio? = null

    @Autowired
    private val usuarioServicio: UsuarioServicio? = null

    @Autowired
    private val equipoServicio: EquipoServicio? = null

    @Autowired
    fun LigaServicio(ligaRepositorio: LigaRepositorio) {
        this.ligaRepositorio = ligaRepositorio
    }

    @Transactional()
    @Throws(DataAccessException::class)
    fun guardarLiga(liga: Liga) {
        ligaRepositorio?.save(liga)
    }

    @Transactional(readOnly = true)
    fun buscarLigaPorNombre(nombreLiga: String): Liga? {
        return ligaRepositorio?.findLigaByName((nombreLiga))
    }

    @Transactional(readOnly = true)
    fun buscarLigaPorId(idLiga: Int): Liga? {
        return ligaRepositorio?.buscarLigaPorId((idLiga))
    }

    @Transactional(readOnly = true)
    fun buscarJugadoresEnLiga(idLiga: Int): Collection<Jugador>? {
        var equipos = equipoServicio?.buscaEquiposDeLigaPorId(idLiga)
        var jugadoresLiga: MutableSet<Jugador> = HashSet()
        if (equipos != null) {
            for (e in equipos) {
                for (j in e.jugadores) {
                    if (j !in jugadoresLiga) {
                        jugadoresLiga.add(j)
                    }
                }
            }
        }
        return jugadoresLiga
    }

    @Transactional(readOnly = true)
    fun calculaPosicionLiga(idLiga: Int, principal: Principal): Int? {
        var equipo = equipoServicio?.buscaMiEquipoEnLiga(idLiga, principal)
        var liga = buscarLigaPorId(idLiga)
        var equiposOrdenadosPuntos = liga?.equipos?.sortedBy { x -> -x.puntos }
        for (e in 0 until equiposOrdenadosPuntos!!.size) {
            if(equiposOrdenadosPuntos[e].name == equipo!!.name) {
                return e+1
            }
        }
        return 1000
    }

    @Transactional(readOnly = true)
    fun buscarJugadoresSinEquipoEnLiga(idLiga: Int): MutableSet<Jugador> {
        var todosJugadores = jugadorServicio?.buscaTodosJugadores()
        var jugadoresConEquipo = buscarJugadoresEnLiga(idLiga)
        var jugadoresSinEquipo: MutableSet<Jugador> = HashSet()
        if (todosJugadores != null) {
            for (tj in todosJugadores) {
                if (tj != null) {
                    if (!jugadoresConEquipo?.contains(tj)!!) {
                        jugadoresSinEquipo.add(tj)
                    }
                }
            }
        }
        return jugadoresSinEquipo
    }

    @Transactional(readOnly = true)
    fun comprobarSiExisteLiga(nombreLiga: String?): Boolean {
        var res = false
        var ligas = ligaRepositorio?.findAll()
        if (ligas != null) {
            for (l in ligas) {
                if (l.name.equals(nombreLiga)) {
                    res = true
                }
            }
        }
        return res
    }

    @Transactional(readOnly = true)
    fun estoyEnLiga(nombreLiga: String, principal: Principal?): Boolean {
        val liga = buscarLigaPorNombre(nombreLiga)
        val nombreUsuario = usuarioServicio?.usuarioLogueado(principal!!)?.user?.username
        return liga!!.usuarios.stream().anyMatch { x -> x.user?.username == nombreUsuario }
    }

}


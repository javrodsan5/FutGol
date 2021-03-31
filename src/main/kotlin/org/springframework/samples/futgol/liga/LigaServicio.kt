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

    @Transactional
    @Throws(DataAccessException::class)
    fun guardarLiga(liga: Liga) {
        ligaRepositorio?.save(liga)
    }

    @Transactional(readOnly = true)
    fun buscarLigaPorNombre(nombreLiga: String): Liga? {
        return ligaRepositorio?.findLigaByName((nombreLiga))
    }

    @Transactional(readOnly = true)
    fun buscarTodasLigas(): Collection<Liga>? {
        return ligaRepositorio?.findAll()
    }

    @Transactional(readOnly = true)
    fun buscarLigaPorId(idLiga: Int): Liga? {
        return ligaRepositorio?.buscarLigaPorId((idLiga))
    }

    @Transactional(readOnly = true)
    fun buscarJugadoresEnLiga(idLiga: Int): Collection<Jugador>? {
        val equipos = equipoServicio?.buscaEquiposDeLigaPorId(idLiga)
        val jugadoresLiga: MutableSet<Jugador> = HashSet()
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
    fun buscarJugadoresSinEquipoEnLiga(idLiga: Int): MutableSet<Jugador> {
        val todosJugadores = jugadorServicio?.buscaTodosJugadores()
        val jugadoresConEquipo = buscarJugadoresEnLiga(idLiga)
        val jugadoresSinEquipo: MutableSet<Jugador> = HashSet()
        if (todosJugadores != null) {
            for (tj in todosJugadores) {
                if (!jugadoresConEquipo?.contains(tj)!!) {
                    jugadoresSinEquipo.add(tj)
                }
            }
        }
        return jugadoresSinEquipo
    }

    @Transactional(readOnly = true)
    fun comprobarSiExisteLiga(nombreLiga: String?): Boolean? {
        return nombreLiga?.let { this.ligaRepositorio?.comprobarSiExisteLiga(it) }
    }

    @Transactional(readOnly = true)
    fun estoyEnLiga(nombreLiga: String, principal: Principal?): Boolean {
        val liga = buscarLigaPorNombre(nombreLiga)
        val nombreUsuario = usuarioServicio?.usuarioLogueado(principal!!)?.user?.username
        return liga!!.usuarios.stream().anyMatch { x -> x.user?.username == nombreUsuario }
    }

}


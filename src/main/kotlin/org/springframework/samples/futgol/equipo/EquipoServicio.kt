package org.springframework.samples.futgol.equipo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.jugador.Jugador
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
    private var ligaServicio: LigaServicio? = null


    @Transactional()
    @Throws(DataAccessException::class)
    fun guardarEquipo(equipo: Equipo) {
        equipoRepositorio?.save(equipo)
    }

    @Transactional(readOnly = true)
    fun buscaEquiposDeLigaPorId(idLiga: Int): Collection<Equipo>? {
        return equipoRepositorio?.buscarEquiposDeLigaPorId((idLiga))
    }

    @Transactional(readOnly = true)
    fun buscarEquipoPorNombre(nombre: String): Equipo? {
        return equipoRepositorio?.buscarEquipoPorNombre(nombre)
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
        var equiposLiga = buscaEquiposDeLigaPorId(idLiga)
        var usuario = usuarioServicio?.usuarioLogueado(principal)
        var miEquipo = Equipo()
        if (equiposLiga != null) {
            for (e in equiposLiga) {
                if (usuario != null) {
                    if (e.usuario!!.user?.username  == usuario.user!!.username) {
                        miEquipo = e
                    }
                }
            }
        }
        return miEquipo
    }

    @Transactional(readOnly = true)
    fun tengoEquipo(idLiga: Int, principal: Principal): Boolean {
        var res = false
        if (StringUtils.hasLength(buscaMiEquipoEnLiga(idLiga, principal).name)) {
            res = true
        }
        return res
    }

    @Transactional(readOnly = true)
    fun comprobarSiExisteEquipoLiga(nombreEquipo: String?, idLiga: Int): Boolean {
        var res = false
        var equipos = equipoRepositorio?.buscarEquiposDeLigaPorId(idLiga)
        if (equipos != null) {
            for (e in equipos) {
                if (e.name.equals(nombreEquipo)) {
                    res = true
                }
            }
        }
        return res
    }

    @Transactional(readOnly = true)
    fun calcularValorEquipo(nombreEquipo: String?, idLiga: Int): Double {
        var equipos = buscaEquiposDeLigaPorId(idLiga)
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
        return valorEquipo
    }

    @Transactional(readOnly = true)
    fun buscaEquiposEnListaEquipos(equipos: MutableSet<Equipo>, nombreEquipo: String?): Equipo {
        var equipo = Equipo()
        for (e in equipos) {
            if(e.name == nombreEquipo) {
                return e
            }
        }
        return equipo
    }

    @Transactional(readOnly = true)
    fun topJugadoresEquipo(nombreEquipo: String?, idLiga: Int): List<Jugador>? {
        var equipos = ligaServicio?.buscarLigaPorId(idLiga)?.equipos
        var equipo = equipos?.let { buscaEquiposEnListaEquipos(it, nombreEquipo) }
        return equipo?.jugadores?.sortedBy { j -> -j.puntos }?.subList(0, 4)

    }
}

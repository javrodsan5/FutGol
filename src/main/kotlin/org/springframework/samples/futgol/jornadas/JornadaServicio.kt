package org.springframework.samples.futgol.jornadas

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.equipoReal.EquipoRealServicio
import org.springframework.samples.futgol.estadisticaJugador.EstadisticaJugador
import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors

@Service
class JornadaServicio {

    private var jornadaRepositorio: JornadaRepositorio? = null


    @Autowired
    fun JornadaServicio(jornadaRepositorio: JornadaRepositorio) {
        this.jornadaRepositorio = jornadaRepositorio
    }

    @Transactional()
    @Throws(DataAccessException::class)
    fun guardarJornada(jornada: Jornada) {
        jornadaRepositorio?.save(jornada)
    }

    @Transactional(readOnly = true)
    fun buscarJornadaPorId(idJornada: Int): Jornada? {
        return jornadaRepositorio?.findJornadaById(idJornada)
    }

    @Transactional(readOnly = true)
    fun buscarJornadaPorNumeroJornada(idJornada: Int): Jornada? {
        return jornadaRepositorio?.findJornadaByNumeroJornada(idJornada)
    }

    @Transactional(readOnly = true)
    fun buscarTodasJornadas(): Collection<Jornada>? {
        return jornadaRepositorio?.findAll()
    }

    @Transactional(readOnly = true)
    fun ordenaJugadoresPuntosJornada(idJornada: Int): MutableList<Jugador?>? {
        var jornada = jornadaRepositorio?.findJornadaById(idJornada)

        var estadisticas: MutableList<EstadisticaJugador> = ArrayList()
        if (jornada != null) {
            for (p in jornada.partidos) {
                for (e in p.estadisticasJugador) {
                    estadisticas.add(e)
                }
            }
        }
        return estadisticas.stream().sorted(Comparator.comparing { j -> -j.puntos }).map { j -> j.jugador }
            .collect(Collectors.toList())
    }

    @Transactional(readOnly = true)
    fun onceIdealJornada(idJornada: Int): Collection<Jugador>? {
        var jugadores: MutableSet<Jugador> = HashSet()
        var defensas: MutableSet<Jugador> = HashSet()
        var centrocampistas: MutableSet<Jugador> = HashSet()
        var delanteros: MutableSet<Jugador> = HashSet()
        var portero = Jugador()
        var jugadoresSortPuntos = ordenaJugadoresPuntosJornada(idJornada)
        if (jugadoresSortPuntos != null) {
            for (jugador in jugadoresSortPuntos) {
                if (jugador != null) {
                    var posicion = jugador?.posicion
                    if (posicion == "PO") {
                        if (portero.name == "") {
                            portero = jugador
                        }
                    } else if (posicion == "DF") {
                        if (defensas.size < 4) {
                            defensas.add(jugador)
                        }
                    } else if (posicion == "CC") {
                        if (centrocampistas.size < 4) {
                            centrocampistas.add(jugador)
                        }
                    } else if (posicion == "DL") {
                        if (delanteros.size < 4) {
                            delanteros.add(jugador)
                        }
                    }
                }
                if (defensas.size >= 4 && centrocampistas.size >= 4 && delanteros.size >= 2 && portero.name != "") {
                    break
                }
            }
        }
        for (j in defensas) {
            jugadores.add(j)
        }
        for (j in centrocampistas) {
            jugadores.add(j)
        }
        for (j in delanteros) {
            jugadores.add(j)
        }
        jugadores.add(portero)

        return jugadores
    }
}

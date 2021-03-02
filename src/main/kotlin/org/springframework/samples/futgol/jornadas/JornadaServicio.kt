package org.springframework.samples.futgol.jornadas

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.equipoReal.EquipoRealServicio
import org.springframework.samples.futgol.estadisticaJugador.EstadisticaJugador
import org.springframework.samples.futgol.estadisticaJugador.EstadisticaJugadorServicio
import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.samples.futgol.usuario.UsuarioServicio
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors

@Service
class JornadaServicio {

    private var jornadaRepositorio: JornadaRepositorio? = null

    @Autowired
    private var estadisticaJugadorServicio: EstadisticaJugadorServicio? = null

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
    fun onceIdealJornada(idJornada: Int): MutableList<Jugador?> {
        var jugadores: MutableList<Jugador?> = ArrayList()
        var defensas: MutableList<Jugador?> = ArrayList()
        var centrocampistas: MutableList<Jugador?> = ArrayList()
        var delanteros: MutableList<Jugador?> = ArrayList()
        var portero = Jugador()
        //var jugadoresSortPuntos = ordenaJugadoresPuntosJornada(idJornada)
        //PROVISIONAL--
        var jugadoresSortPuntos= this.estadisticaJugadorServicio?.buscarTodasEstadisticas()
            ?.stream()?.filter { x-> x.partido?.jornada?.id == idJornada}
            ?.sorted(Comparator.comparing { x-> x.puntos })
            ?.map { x-> x.jugador }?.collect(Collectors.toList())?.reversed()
        //--
        if (jugadoresSortPuntos != null && !jugadoresSortPuntos.isEmpty()) {

            portero = jugadoresSortPuntos?.stream()?.filter { x -> x?.posicion == "PO" }?.findFirst()?.get()!!
            defensas = jugadoresSortPuntos?.stream()?.filter { x -> x?.posicion == "DF" }.collect(Collectors.toList())
                .subList(0, 4)
            centrocampistas =
                jugadoresSortPuntos?.stream()?.filter { x -> x?.posicion == "CC" }.collect(Collectors.toList())
                    .subList(0, 4)
            delanteros = jugadoresSortPuntos?.stream()?.filter { x -> x?.posicion == "DL" }.collect(Collectors.toList())
                .subList(0, 2)
            jugadores.add(portero)
            jugadores.addAll(defensas)
            jugadores.addAll(centrocampistas)
            jugadores.addAll(delanteros)
        }


        return jugadores
    }
}

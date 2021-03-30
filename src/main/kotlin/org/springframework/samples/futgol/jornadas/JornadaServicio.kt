package org.springframework.samples.futgol.jornadas

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.equipoReal.EquipoRealServicio
import org.springframework.samples.futgol.estadisticaJugador.EstadisticaJugadorServicio
import org.springframework.samples.futgol.jugador.Jugador
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

    @Transactional()
    fun onceIdeal(
        jugadores: List<Jugador?>?,
        idJornada: Int,
        nombreEquipo: String
    ): Map<String, MutableList<Jugador?>> {
        var jug442: MutableList<Jugador?> = ArrayList()
        var jug433: MutableList<Jugador?> = ArrayList()
        var jug352: MutableList<Jugador?> = ArrayList()
        var jug532: MutableList<Jugador?> = ArrayList()
        var jug451: MutableList<Jugador?> = ArrayList()


        var map: Map<String, MutableList<Jugador?>>

        var portero = jugadores?.filter { x -> x?.posicion == "PO" }?.get(0)!!
        var defensas = jugadores.filter { x -> x?.posicion == "DF" }
        var centrocampistas = jugadores.filter { x -> x?.posicion == "CC" }
        var delanteros = jugadores.filter { x -> x?.posicion == "DL" }
        var numeroDefensas = defensas.size
        var numeroCentroCampistas = centrocampistas.size
        var numeroDelanteros = delanteros.size

        if (numeroDefensas >= 4 && numeroCentroCampistas >= 4 && numeroDelanteros >= 2) {

            jug442.add(portero)
            jug442.addAll(defensas.subList(0, 4))
            jug442.addAll(centrocampistas.subList(0, 4))
            jug442.addAll(delanteros.subList(0, 2))

        }
        if (numeroDefensas >= 4 && numeroCentroCampistas >= 3 && numeroDelanteros >= 3) {

            jug433.add(portero)
            jug433.addAll(defensas.subList(0, 4))
            jug433.addAll(centrocampistas.subList(0, 3))
            jug433.addAll(delanteros.subList(0, 3))

        }

        if (numeroDefensas >= 3 && numeroCentroCampistas >= 5 && numeroDelanteros >= 2) {
            jug352.add(portero)
            jug352.addAll(defensas.subList(0, 3))
            jug352.addAll(centrocampistas.subList(0, 5))
            jug352.addAll(delanteros.subList(0, 2))

        }

        if (numeroDefensas >= 5 && numeroCentroCampistas >= 3 && numeroDelanteros >= 2) {

            jug532.add(portero)
            jug532.addAll(defensas.subList(0, 5))
            jug532.addAll(centrocampistas.subList(0, 3))
            jug532.addAll(delanteros.subList(0, 2))
        }

        if (numeroDefensas >= 4 && numeroCentroCampistas >= 5 && numeroDelanteros >= 1) {

            jug451.add(portero)
            jug451.addAll(defensas.subList(0, 4))
            jug451.addAll(centrocampistas.subList(0, 5))
            jug451.add(delanteros[0])
        }

        var puntos442 = 0
        var puntos433 = 0
        var puntos352 = 0
        var puntos532 = 0
        var puntos451 = 0

        if (idJornada != 0 && nombreEquipo == "") { //significa que es jornadas y no equipo real
            for (n in 0 until jug442.size) {
                puntos442 += jug442[n]?.id?.let {
                    this.estadisticaJugadorServicio?.buscarEstadisticasPorJugadorJornada(it, idJornada)?.puntos
                }!!
                puntos433 += jug433[n]?.id?.let {
                    this.estadisticaJugadorServicio?.buscarEstadisticasPorJugadorJornada(it, idJornada)?.puntos
                }!!
                puntos352 += jug352[n]?.id?.let {
                    this.estadisticaJugadorServicio?.buscarEstadisticasPorJugadorJornada(it, idJornada)?.puntos
                }!!
                puntos532 += jug532[n]?.id?.let {
                    this.estadisticaJugadorServicio?.buscarEstadisticasPorJugadorJornada(it, idJornada)?.puntos
                }!!
                puntos451 += jug451[n]?.id?.let {
                    this.estadisticaJugadorServicio?.buscarEstadisticasPorJugadorJornada(it, idJornada)?.puntos
                }!!
            }
        } else {
            puntos442 = jug442.sumBy { x -> x?.puntos!! }
            puntos433 = jug433.sumBy { x -> x?.puntos!! }
            puntos352 = jug352.sumBy { x -> x?.puntos!! }
            puntos532 = jug532.sumBy { x -> x?.puntos!! }
            puntos451 = jug451.sumBy { x -> x?.puntos!! }

        }

        return if (puntos352 >= puntos433 && puntos352 >= puntos442 && puntos352 >= puntos532 && puntos352>= puntos451) {
            map = mapOf("3-5-2" to jug352)
            map
        } else if (puntos442 >= puntos433 && puntos442 >= puntos352 && puntos442 >= puntos532 && puntos442 >= puntos451) {
            map = mapOf("4-4-2" to jug442)
            map
        } else if (puntos433 >= puntos352 && puntos433 >= puntos442 && puntos433 >= puntos532 && puntos433 >= puntos451) {
            map = mapOf("4-3-3" to jug433)
            map
        }else if (puntos451 >= puntos352 && puntos451 >= puntos442 && puntos451 >= puntos532 && puntos451>=puntos433) {
            map = mapOf("4-5-1" to jug451)
            map
        } else {
            map = mapOf("5-3-2" to jug532)
            map
        }
    }
}


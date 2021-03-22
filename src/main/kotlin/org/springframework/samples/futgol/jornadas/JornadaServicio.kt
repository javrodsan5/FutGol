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
    private var equipoRealServicio: EquipoRealServicio? = null

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
        var jug442: MutableList<Jugador?> = ArrayList();
        var map: Map<String, MutableList<Jugador?>>

        var jug433: MutableList<Jugador?> = ArrayList();
        var jug352: MutableList<Jugador?> = ArrayList()
        var jug532: MutableList<Jugador?> = ArrayList()

        var portero: Jugador

        var defensas442: MutableList<Jugador?>
        var centrocampistas442: MutableList<Jugador?>
        var delanteros442: MutableList<Jugador?>

        var defensas433: MutableList<Jugador?> = ArrayList()
        var centrocampistas433: MutableList<Jugador?>
        var delanteros433: MutableList<Jugador?>

        var defensas352: MutableList<Jugador?>
        var centrocampistas352: MutableList<Jugador?>
        var delanteros352: MutableList<Jugador?> = ArrayList()

        var defensas532: MutableList<Jugador?>
        var centrocampistas532: MutableList<Jugador?>
        var delanteros532: MutableList<Jugador?> = ArrayList()

        portero = jugadores?.stream()?.filter { x -> x?.posicion == "PO" }?.findFirst()?.get()!!

        defensas442 =
            jugadores.stream().filter { x -> x?.posicion == "DF" }.collect(Collectors.toList())
                .subList(0, 4)
        centrocampistas442 =
            jugadores.stream().filter { x -> x?.posicion == "CC" }.collect(Collectors.toList())
                .subList(0, 4)
        delanteros442 =
            jugadores.stream().filter { x -> x?.posicion == "DL" }.collect(Collectors.toList())
                .subList(0, 2)
        jug442.add(portero)
        jug442.addAll(defensas442)
        jug442.addAll(centrocampistas442)
        jug442.addAll(delanteros442)

        defensas433.addAll(defensas442)
        centrocampistas433 =
            jugadores.stream().filter { x -> x?.posicion == "CC" }.collect(Collectors.toList())
                .subList(0, 3)
        delanteros433 =
            jugadores.stream().filter { x -> x?.posicion == "DL" }.collect(Collectors.toList())
                .subList(0, 3)
        jug433.add(portero)
        jug433.addAll(defensas433)
        jug433.addAll(centrocampistas433)
        jug433.addAll(delanteros433)

        defensas352 =
            jugadores.stream().filter { x -> x?.posicion == "DF" }.collect(Collectors.toList())
                .subList(0, 3)
        centrocampistas352 =
            jugadores.stream().filter { x -> x?.posicion == "CC" }.collect(Collectors.toList())
                .subList(0, 5)
        delanteros352.addAll(delanteros442)
        jug352.add(portero)
        jug352.addAll(defensas352)
        jug352.addAll(centrocampistas352)
        jug352.addAll(delanteros352)

        defensas532 =
            jugadores.stream().filter { x -> x?.posicion == "DF" }.collect(Collectors.toList())
                .subList(0, 5)
        centrocampistas532 =
            jugadores.stream().filter { x -> x?.posicion == "CC" }.collect(Collectors.toList())
                .subList(0, 3)
        delanteros532.addAll(delanteros442)
        jug532.add(portero)
        jug532.addAll(defensas532)
        jug532.addAll(centrocampistas532)
        jug532.addAll(delanteros532)

        var puntos442 = 0
        var puntos433 = 0
        var puntos352 = 0
        var puntos532 = 0
        if (idJornada != 0 && nombreEquipo == "") { //significa que es jornadas y no equipo real
            for (n in 0 until jug442.size) {
                var p442 = jug442[n]?.id?.let {
                    estadisticaJugadorServicio?.buscarEstadisticasPorJugador(it)?.stream()
                        ?.filter { x -> x.partido?.jornada?.numeroJornada == idJornada }
                        ?.map { x -> x.puntos }?.findFirst()?.get()
                }!!
                puntos442 += p442
                var p433 = jug433[n]?.id?.let {
                    estadisticaJugadorServicio?.buscarEstadisticasPorJugador(it)?.stream()
                        ?.filter { x -> x.partido?.jornada?.numeroJornada == idJornada }
                        ?.map { x -> x.puntos }?.findFirst()?.get()
                }!!
                puntos433 += p433
                var p352 = jug352[n]?.id?.let {
                    estadisticaJugadorServicio?.buscarEstadisticasPorJugador(it)?.stream()
                        ?.filter { x -> x.partido?.jornada?.numeroJornada == idJornada }
                        ?.map { x -> x.puntos }?.findFirst()?.get()
                }!!
                puntos352 += p352
                var p532 = jug532[n]?.id?.let {
                    estadisticaJugadorServicio?.buscarEstadisticasPorJugador(it)?.stream()
                        ?.filter { x -> x.partido?.jornada?.numeroJornada == idJornada }
                        ?.map { x -> x.puntos }?.findFirst()?.get()
                }!!
                puntos532 += p532
            }
        } else {
            puntos442 = jug442.stream().mapToInt { x -> x?.puntos!! }.sum()
            puntos433 = jug433.stream().mapToInt { x -> x?.puntos!! }.sum()
            puntos352 = jug352.stream().mapToInt { x -> x?.puntos!! }.sum()
            puntos532 = jug532.stream().mapToInt { x -> x?.puntos!! }.sum()

        }

        return if (puntos352 >= puntos433 && puntos352 >= puntos442 && puntos352 >= puntos532) {
            map = mapOf("3-5-2" to jug352)
            map
        } else if (puntos442 >= puntos433 && puntos442 >= puntos352 && puntos442 >= puntos532) {
            map = mapOf("4-4-2" to jug442)
            map
        } else if (puntos433 >= puntos352 && puntos433 >= puntos442 && puntos433 >= puntos532) {
            map = mapOf("4-3-3" to jug433)
            map
        } else {
            map = mapOf("5-3-2" to jug532)
            map
        }
        return map
    }
}


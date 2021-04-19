package org.springframework.samples.futgol.subasta

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.samples.futgol.jugador.JugadorServicio
import org.springframework.samples.futgol.liga.LigaServicio
import org.springframework.samples.futgol.movimientos.Movimiento
import org.springframework.samples.futgol.movimientos.MovimientoServicio
import org.springframework.samples.futgol.puja.PujaServicio
import org.springframework.samples.futgol.util.MetodosAux
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.text.NumberFormat
import java.util.*
import java.util.stream.Collectors
import kotlin.Comparator

@Service
class SubastaServicio {

    private var subastaRepositorio: SubastaRepositorio? = null

    @Autowired
    private val ligaServicio: LigaServicio? = null

    @Autowired
    private val movimientoServicio: MovimientoServicio? = null

    @Autowired
    private val jugadorServicio: JugadorServicio? = null

    @Autowired
    private val pujaServicio: PujaServicio? = null

    @Autowired
    private val equipoServicio: EquipoServicio? = null

    @Autowired
    fun SubastaServicio(subastaRepositorio: SubastaRepositorio) {
        this.subastaRepositorio = subastaRepositorio
    }

    @Transactional
    @Throws(DataAccessException::class)
    fun guardarSubasta(subasta: Subasta) {
        subastaRepositorio?.save(subasta)
    }

    @Transactional(readOnly = true)
    fun buscarSubastaPorLigaId(idLiga: Int): Subasta? {
        return subastaRepositorio?.findSubastaByLigaId(idLiga)
    }

    @Transactional
    fun borraSubastaPorLigaId(idLiga: Int): Unit? {
        return subastaRepositorio?.removeSubastaByLigaId(idLiga)
    }

    @Transactional(readOnly = true)
    fun existeSubastaPorLigaId(idLiga: Int): Boolean? {
        return subastaRepositorio?.existeSubastaLiga(idLiga)
    }

    @Transactional(readOnly = true)
    fun estaJugadorEnSubasta(subasta: Subasta, idJugador: Int): Boolean? {
        val jugador = jugadorServicio!!.buscaJugadorPorId(idJugador)
        return subasta.jugadores.contains(jugador)
    }


    @Transactional
    fun subasta(idLiga: Int) {
        if (existeSubastaPorLigaId(idLiga) == true) {
            borraSubastaPorLigaId(idLiga)
        }
        val liga = ligaServicio!!.buscarLigaPorId(idLiga)
        var subasta = Subasta()
        subasta.liga = liga
        subasta.jugadores = liga?.id?.let {
            ligaServicio.buscarJugadoresSinEquipoEnLiga(it).shuffled().stream().limit(15)
                .collect(Collectors.toList())
        }!!
        guardarSubasta(subasta)
    }

    @Transactional
    fun ganarSubasta(idLiga: Int) {
        var subasta = buscarSubastaPorLigaId(idLiga)!!

        for (j in subasta.jugadores) {
            if (subasta.id?.let { j.id?.let { it1 -> pujaServicio!!.existePujaJugSub(it1, it) } } == true) {
                val idSubasta = buscarSubastaPorLigaId(idLiga)?.id!!
                var pujaMayor = j.id?.let { pujaServicio?.buscarPujasJugadorSubasta(it, idSubasta) }!!.stream()
                    .max(Comparator.comparing { x -> x.cantidad }).orElse(null)

                if (pujaMayor.cantidad >= (j.valor * 1000000)) {
                    var movimiento = Movimiento()
                    var liga = this.ligaServicio?.buscarLigaPorId(idLiga)
                    movimiento.jugador = j
                    movimiento.liga = liga
                    movimiento.creadorMovimiento = pujaMayor.equipo?.usuario

                    movimiento.texto =
                        pujaMayor.equipo?.usuario?.user?.username + " ha comprado a " + j.name + " por " + MetodosAux().enteroAEuros(
                            pujaMayor.cantidad
                        ) + "."
                    movimiento.textoPropio =
                        "Has comprado a " + j.name + " por " + MetodosAux().enteroAEuros(pujaMayor.cantidad) + "."

                    var equipoPujaMayor = pujaMayor.equipo!!
                    equipoPujaMayor.jugadores.add(j)
                    equipoPujaMayor.jugBanquillo.add(j)

                    var money = equipoPujaMayor.dineroRestante - pujaMayor.cantidad
                    equipoPujaMayor.dineroRestante = money
                    equipoServicio!!.guardarEquipo(equipoPujaMayor)
                    movimientoServicio?.guardarMovimiento(movimiento)

                } else {
                    noPujasJugador(idLiga, j)
                }
            } else {
                noPujasJugador(idLiga, j)
            }
        }
    }

    fun noPujasJugador(idLiga: Int, j: Jugador) {
        var equiposLiga = equipoServicio?.buscaEquiposDeLigaPorId(idLiga)!!
        for (eq in equiposLiga) {
            if (j.id?.let { eq.id?.let { it1 -> jugadorServicio!!.existeJugadorEnEquipo(it, it1) } } == true) {
                eq.jugadores.removeIf { it.id == j.id }
                eq.jugBanquillo.removeIf { it.id == j.id }
                eq.onceInicial.removeIf { it.id == j.id }

                var dineroNuevo = eq.dineroRestante + j.valor * 1000000
                eq.dineroRestante = dineroNuevo.toInt()
            }
            equipoServicio.guardarEquipo(eq)
        }
    }

    @Transactional
    fun autoGanaryGenerarSubasta() {
        var ligas = this.ligaServicio?.buscarTodasLigas()!!
        for (l in ligas) {
            l.id?.let { ganarSubasta(it) }
            l.id?.let { subasta(it) }
        }
    }

    @Transactional
    fun sacarJugadorSubasta(idJugador: Int, subasta: Subasta) {
        if (estaJugadorEnSubasta(subasta, idJugador) == false)
            subasta.jugadores.add(jugadorServicio?.buscaJugadorPorId(idJugador)!!)
    }
}

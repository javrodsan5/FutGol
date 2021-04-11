package org.springframework.samples.futgol.subasta

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.equipo.EquipoServicio
import org.springframework.samples.futgol.jugador.Jugador
import org.springframework.samples.futgol.jugador.JugadorServicio
import org.springframework.samples.futgol.liga.LigaServicio
import org.springframework.samples.futgol.puja.PujaServicio
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors

@Service
class SubastaServicio {

    private var subastaRepositorio: SubastaRepositorio? = null

    @Autowired
    private val ligaServicio: LigaServicio? = null

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

                var pujaMayor = j.id?.let { pujaServicio?.buscarPujasJugadorLiga(it, idLiga) }!!.stream()
                    .max(Comparator.comparing { x -> x.cantidad }).orElse(null)

                if (pujaMayor.cantidad > (j.valor * 1000000)) {
                    var equipoPujaMayor = pujaMayor.equipo!!
                    equipoPujaMayor.jugadores.add(j)
                    equipoPujaMayor.jugBanquillo.add(j)

                    var money = equipoPujaMayor.dineroRestante - pujaMayor.cantidad
                    equipoPujaMayor.dineroRestante = money
                    equipoServicio!!.guardarEquipo(equipoPujaMayor)
                }else {
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
                //Revisar los remove
                eq.jugadores.remove(j)
                if (j in eq.jugBanquillo) eq.jugBanquillo.remove(j)
                if (j in eq.onceInicial) eq.onceInicial.remove(j)

                var dineroNuevo = eq.dineroRestante + j.valor
                eq.dineroRestante = dineroNuevo.toInt()
            }
            equipoServicio!!.guardarEquipo(eq)
        }
    }


    fun autoGanaryGenerarSubasta() {
        var ligas = this.ligaServicio?.buscarTodasLigas()!!
        for (l in ligas) {
            l.id?.let { ganarSubasta(it) }
            l.id?.let { subasta(it) }
        }
    }

    @Transactional
    fun sacarJugadorSubasta(idJugador: Int, subasta: Subasta) {
        subasta.jugadores.add(jugadorServicio?.buscaJugadorPorId(idJugador)!!)
    }
}
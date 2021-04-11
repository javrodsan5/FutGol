package org.springframework.samples.futgol.subasta

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.jugador.JugadorServicio
import org.springframework.samples.futgol.liga.LigaServicio
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

    fun autoNuevaSubasta() {
        var ligas = this.ligaServicio?.buscarTodasLigas()!!
        for (l in ligas) {
            l.id?.let { subasta(it) }
        }
    }

    @Transactional
    fun sacarJugadorSubasta(idJugador: Int, subasta: Subasta) {
        subasta.jugadores.add(jugadorServicio?.buscaJugadorPorId(idJugador)!!)
    }
}

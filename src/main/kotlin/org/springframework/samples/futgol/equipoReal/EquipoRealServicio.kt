package org.springframework.samples.futgol.equipoReal

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.equipo.Equipo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EquipoRealServicio {

    private var equipoRealRepositorio: EquipoRealRepositorio? = null

    @Autowired
    fun EquipoServicio(equipoRealRepositorio: EquipoRealRepositorio) {
        this.equipoRealRepositorio = equipoRealRepositorio
    }

    @Transactional()
    @Throws(DataAccessException::class)
    fun guardarEquipo(equipo: EquipoReal) {
        equipoRealRepositorio?.save(equipo)
    }

    @Transactional(readOnly = true)
    fun buscarTodosEquiposReales(): Collection<EquipoReal>? {
        return equipoRealRepositorio?.findAll()
    }

    @Transactional(readOnly = true)
    fun buscarEquipoRealPorNombre(nombre: String): EquipoReal? {
        return equipoRealRepositorio?.buscarEquipoRealPorNombre(nombre)
    }

}

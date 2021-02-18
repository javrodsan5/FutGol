package org.springframework.samples.futgol.equipo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.samples.futgol.liga.LigaRepositorio
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EquipoServicio {

    private var equipoRepositorio: EquipoRepositorio? = null

    @Autowired
    fun EquipoServicio(equipoRepositorio: EquipoRepositorio) {
        this.equipoRepositorio = equipoRepositorio
    }

    @Transactional()
    @Throws(DataAccessException::class)
    fun saveEquipo(equipo: Equipo) {
        equipoRepositorio?.save(equipo)
    }


}

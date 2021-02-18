package org.springframework.samples.futgol.equipo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
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


    @Transactional()
    @Throws(DataAccessException::class)
    fun saveEquipo(equipo: Equipo) {
        equipoRepositorio?.save(equipo)
    }

    @Transactional(readOnly = true)
    fun findEquiposDeLigaPorId(idLiga: Int): Collection<Equipo>? {
        return equipoRepositorio?.buscarEquiposDeLigaPorId((idLiga))
    }

    @Transactional(readOnly = true)
    fun buscaMiEquipoEnLiga(idLiga: Int, principal: Principal): Equipo {
        var equiposLiga = findEquiposDeLigaPorId(idLiga)
        var usuario = usuarioServicio?.usuarioLogueado(principal)
        var miEquipo = Equipo()
        if (equiposLiga != null) {
            for (e in equiposLiga) {
                if (usuario != null) {
                    if (e.usuario!!.username == usuario.user!!.username) {
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


}

package org.springframework.samples.futgol.puja

import org.springframework.data.repository.Repository

interface PujaRepositorio : Repository<Puja, Int> {


    fun save(puja: Puja)

    fun findPujaById(id: Int): Puja
}

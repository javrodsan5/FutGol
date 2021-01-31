/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.futgol.liga

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.samples.futgol.usuario.Usuario
import org.springframework.transaction.annotation.Transactional

interface LigaRepositorio : Repository<Liga, Int> {


    fun save(liga: Liga)

    fun findLigaByName(nombre : String): Liga

//    @Query("SELECT l.usuarios FROM Liga l where l.name = ?1")
//    fun buscarUsuariosEnLiga(nombreLiga: String): Collection<Usuario>

}

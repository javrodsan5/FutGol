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

import org.springframework.util.StringUtils
import org.springframework.validation.Errors
import org.springframework.validation.Validator

class LigaValidador : Validator {

    private val REQUERIDO = "Campo requerido."

    override fun validate(target: Any, errors: Errors) {
        var liga = target as Liga
        var name = liga.name

        if (!StringUtils.hasLength(name)) {
            errors.rejectValue("name", REQUERIDO, REQUERIDO)
        }
    }

    override fun supports(clazz: Class<*>): Boolean {
        return Liga::class.java.isAssignableFrom(clazz)
    }


}

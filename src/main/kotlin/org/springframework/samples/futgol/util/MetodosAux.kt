package org.springframework.samples.futgol.util

class MetodosAux {


    open fun transformador(posicion: String): Int {
        var res: Int
        res = if (posicion == "PO") {
            15
        } else if (posicion == "DF") {
            10
        } else if (posicion == "CC") {
            5
        } else {
            0
        }
        return res
    }
}

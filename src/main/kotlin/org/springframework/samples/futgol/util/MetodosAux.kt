package org.springframework.samples.futgol.util

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

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

    open fun leerFichero(ruta: String): List<String?> {
        var l: List<String?> = ArrayList()
        try {
            l = Files.lines(Paths.get(ruta)).collect(Collectors.toList())
        } catch (e: IOException) {
            println("No se puede leer el fichero.")
        }
        return l
    }

    open fun modificarNombreJugador(l: List<String?>, equipo: String, nombreJugador: String): String {
        var res = nombreJugador
        for (element in l) {
            var linea = element?.split(",")
            if (linea?.size!! >= 3) {
                if (linea[2] == equipo && linea[0] == nombreJugador) {
                    res = linea[1]
                }
            } else {
                if (linea[0] == nombreJugador) {
                    res = linea[1]
                }
            }
        }
        return res
    }

    open fun modificarNombreJugadorFcStats(
        l: List<String?>,
        equipo: String,
        l2: MutableList<String>?,
        r: Int
    ): MutableList<String>? {
        for (element in l) {
            var linea = element?.split(",")
            if (l2 != null) {
                if (linea?.size!! >= 3) {
                    if (linea[2] == equipo && linea[0] == l2[r]
                    ) {
                        l2.removeAt(r)
                        l2.add(r, linea[1])
                    }
                } else {
                    if (linea[0] == l2[r]) {
                        l2.removeAt(r)
                        l2.add(r, linea[1])
                    }
                }
            }
        }
        return l2
    }
}

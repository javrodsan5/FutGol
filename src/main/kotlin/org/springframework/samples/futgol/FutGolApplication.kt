package org.springframework.samples.futgol

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication(proxyBeanMethods = false)
class FutGolApplication

fun main(args: Array<String>) {
    runApplication<FutGolApplication>(*args)
}

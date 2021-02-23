package org.springframework.samples.futgol

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.text.Normalizer
import javax.sql.DataSource


@SpringBootApplication(proxyBeanMethods = false)
class FutGolApplication


fun main(args: Array<String>) {
    runApplication<FutGolApplication>(*args)
}

@Bean
fun dataSource(): DataSource? {
    val dataSourceBuilder = DataSourceBuilder.create()
    dataSourceBuilder.driverClassName("org.sqlite.JDBC")
    dataSourceBuilder.url("jdbc:sqlite:your.db")
    return dataSourceBuilder.build()
}




package org.springframework.samples.futgol.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.view.InternalResourceViewResolver
import javax.sql.DataSource


@Configuration
@EnableWebSecurity
class WebConfig : WebMvcConfigurer {

    @Bean
    fun viewResolver(): InternalResourceViewResolver? {
        var bean: InternalResourceViewResolver = InternalResourceViewResolver()
        bean.setPrefix("/WEB-INF/views/")
        bean.setSuffix(".jsp")
        return bean

    }

}

package cz.fb.manaus.spring

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
@ComponentScan(value = ["cz.fb.manaus.rest"])
open class RestLocalConfiguration(private val objectMapper: ObjectMapper) : WebMvcConfigurer {

    override fun configurePathMatch(configurer: PathMatchConfigurer) {
        configurer.isUseSuffixPatternMatch = false
    }

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>?) {
        converters?.add(mappingJackson2HttpMessageConverter())
        super.configureMessageConverters(converters)
    }

    @Bean
    open fun mappingJackson2HttpMessageConverter(): MappingJackson2HttpMessageConverter {
        val jsonConverter = MappingJackson2HttpMessageConverter()
        jsonConverter.objectMapper = objectMapper
        return jsonConverter
    }
}

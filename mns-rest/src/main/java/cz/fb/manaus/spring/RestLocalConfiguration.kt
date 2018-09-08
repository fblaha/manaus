package cz.fb.manaus.spring

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.text.SimpleDateFormat
import java.util.*

@Configuration
@ComponentScan(value = ["cz.fb.manaus.rest"])
open class RestLocalConfiguration : WebMvcConfigurer {

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.add(MappingJackson2HttpMessageConverter(objectMapper()))
    }

    override fun configurePathMatch(configurer: PathMatchConfigurer) {
        configurer.isUseSuffixPatternMatch = false
    }

    @Bean
    open fun objectMapper(): ObjectMapper {
        val builder = Jackson2ObjectMapperBuilder()
        builder.indentOutput(true).dateFormat(SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
        builder.timeZone(TimeZone.getDefault())
        return builder.build()
    }
}

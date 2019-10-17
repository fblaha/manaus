package cz.fb.manaus.spring.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.util.logging.Logger
import javax.annotation.PostConstruct

@ConstructorBinding
@ConfigurationProperties("db")
data class DatabaseConf(val file: String? = null) {

    private val log = Logger.getLogger(DatabaseConf::class.simpleName)

    @PostConstruct
    fun log() {
        log.info { "$this" }
    }
}

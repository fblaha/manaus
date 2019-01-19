package cz.fb.manaus.spring.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.logging.Logger
import javax.annotation.PostConstruct

@ConfigurationProperties("db")
data class DatabaseConf(var file: String? = null) {

    private val log = Logger.getLogger(DatabaseConf::class.simpleName)

    @PostConstruct
    fun log() {
        log.info { "$this" }
    }
}

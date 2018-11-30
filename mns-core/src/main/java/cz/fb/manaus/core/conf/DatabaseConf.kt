package cz.fb.manaus.core.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.util.logging.Logger
import javax.annotation.PostConstruct

@Component
@ConfigurationProperties("db")
data class DatabaseConf(var file: String? = null) {

    private val log = Logger.getLogger(DatabaseConf::class.java.simpleName)

    @PostConstruct
    fun log() {
        log.info("$this")
    }
}

package cz.fb.manaus.spring.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.logging.Logger
import javax.annotation.PostConstruct

@ConfigurationProperties("betting")
data class BettingConf(var disabledListeners: List<String> = emptyList()) {

    private val log = Logger.getLogger(BettingConf::class.java.simpleName)

    @PostConstruct
    fun log() {
        log.info("$this")
    }
}
package cz.fb.manaus.reactor.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.util.logging.Logger
import javax.annotation.PostConstruct

@Component
@ConfigurationProperties("betting")
data class BettingConf(var disabledListeners: List<String> = emptyList()) {

    private val log = Logger.getLogger(BettingConf::class.java.simpleName)

    @PostConstruct
    fun log() {
        log.info("$this")
    }
}

package cz.fb.manaus.spring.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration
import java.util.logging.Logger
import javax.annotation.PostConstruct

@ConfigurationProperties("markets")
data class MarketConf(var history: Duration = Duration.ZERO,
                      var lookAhead: Duration = Duration.ZERO) {

    private val log = Logger.getLogger(MarketConf::class.simpleName)

    @PostConstruct
    fun validate() {
        log.info { "$this" }
        check(!history.isZero)
        check(!lookAhead.isZero)
    }
}

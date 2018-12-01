package cz.fb.manaus.spring.conf

import com.google.common.base.Preconditions
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration
import java.util.logging.Logger
import javax.annotation.PostConstruct

@ConfigurationProperties("markets")
data class MarketConf(var history: Duration = Duration.ZERO,
                      var lookAhead: Duration = Duration.ZERO) {

    private val log = Logger.getLogger(MarketConf::class.java.simpleName)

    @PostConstruct
    fun validate() {
        Preconditions.checkState(!history.isZero)
        Preconditions.checkState(!lookAhead.isZero)
        log.info("$this")
    }
}

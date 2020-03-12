package cz.fb.manaus.spring.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.time.Duration
import java.util.logging.Logger
import javax.annotation.PostConstruct

@ConstructorBinding
@ConfigurationProperties("markets")
data class MarketConf(
        val history: Duration = Duration.ZERO
) {

    private val log = Logger.getLogger(MarketConf::class.simpleName)

    @PostConstruct
    fun validate() {
        log.info { "$this" }
        check(!history.isZero)
    }
}

package cz.fb.manaus.spring.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.logging.Logger
import javax.annotation.PostConstruct

@ConfigurationProperties("betting")
data class BettingConf(var disabledListeners: List<String> = emptyList(),
                       var amount: Double = -1.0) {

    private val log = Logger.getLogger(BettingConf::class.simpleName)

    @PostConstruct
    fun validate() {
        log.info { "$this" }
        check(amount > 0.0)
    }

}

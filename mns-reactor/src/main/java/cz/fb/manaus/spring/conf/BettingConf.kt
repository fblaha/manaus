package cz.fb.manaus.spring.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.util.logging.Logger
import javax.annotation.PostConstruct

@ConstructorBinding
@ConfigurationProperties("betting")
data class BettingConf(val disabledListeners: List<String> = emptyList(),
                       val amount: Double = -1.0) {

    private val log = Logger.getLogger(BettingConf::class.simpleName)

    @PostConstruct
    fun validate() {
        log.info { "$this" }
        check(amount > 0.0)
    }

}

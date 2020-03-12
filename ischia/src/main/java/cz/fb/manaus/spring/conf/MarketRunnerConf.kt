package cz.fb.manaus.spring.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.util.logging.Logger
import javax.annotation.PostConstruct

@ConstructorBinding
@ConfigurationProperties("market-runner")
data class MarketRunnerConf(val runnerName: String?) {

    private val log = Logger.getLogger(MarketRunnerConf::class.simpleName)

    @PostConstruct
    fun validate() {
        log.info { "$this" }
    }
}
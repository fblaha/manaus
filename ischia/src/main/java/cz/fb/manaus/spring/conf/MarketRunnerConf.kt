package cz.fb.manaus.spring.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.logging.Logger
import javax.annotation.PostConstruct

@ConfigurationProperties("market-runner")
data class MarketRunnerConf(var runnerName: String?,
                            var types: List<String>?) {

    private val log = Logger.getLogger(MarketRunnerConf::class.simpleName)

    @PostConstruct
    fun validate() {
        log.info { "$this" }
        check(!types.isNullOrEmpty())
    }
}
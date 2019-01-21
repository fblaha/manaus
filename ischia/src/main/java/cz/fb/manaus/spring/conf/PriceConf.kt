package cz.fb.manaus.spring.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.logging.Logger
import javax.annotation.PostConstruct

@ConfigurationProperties("price")
data class PriceConf(var downgradeBackRate: Double = 0.0,
                     var downgradeLayRate: Double = 0.0,
                     var bulldoze: Double = 0.0,
                     var min: Double = 0.0,
                     var max: Double = 0.0,
                     var limit: Int = 0) {

    private val log = Logger.getLogger(PriceConf::class.simpleName)

    @PostConstruct
    fun validate() {
        log.info { "$this" }
        check(downgradeBackRate > 0.0)
        check(downgradeLayRate > 0.0)
        check(min >= 1.0)
        check(max > 1.0)
        check(limit > 0)
    }
}
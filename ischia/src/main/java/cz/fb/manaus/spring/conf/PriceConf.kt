package cz.fb.manaus.spring.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.util.logging.Logger
import javax.annotation.PostConstruct

@ConstructorBinding
@ConfigurationProperties("price")
data class PriceConf(val downgradeBackRate: Double = 0.0,
                     val downgradeLayRate: Double = 0.0,
                     val bulldoze: Double = 0.0,
                     val min: Double = 0.0,
                     val max: Double = 0.0,
                     val limit: Int = 0) {

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
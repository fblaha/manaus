package cz.fb.manaus.spring.conf

import com.google.common.base.Preconditions
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

    private val log = Logger.getLogger(PriceConf::class.java.simpleName)

    @PostConstruct
    fun validate() {
        log.info("$this")
        Preconditions.checkState(downgradeBackRate > 0.0)
        Preconditions.checkState(downgradeLayRate > 0.0)
        Preconditions.checkState(min >= 1.0)
        Preconditions.checkState(max > 1.0)
        Preconditions.checkState(limit > 0)
    }
}
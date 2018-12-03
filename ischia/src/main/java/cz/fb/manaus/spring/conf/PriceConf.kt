package cz.fb.manaus.spring.conf

import com.google.common.base.Preconditions
import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.logging.Logger
import javax.annotation.PostConstruct

@ConfigurationProperties("price")
data class PriceConf(var downgradeBackRate: Double = 0.0,
                     var downgradeLayRate: Double = 0.0,
                     var bulldoze: Double = 0.0) {

    private val log = Logger.getLogger(PriceConf::class.java.simpleName)

    @PostConstruct
    fun validate() {
        log.info("$this")
        Preconditions.checkState(downgradeBackRate > 0.0)
        Preconditions.checkState(downgradeLayRate > 0.0)
    }
}
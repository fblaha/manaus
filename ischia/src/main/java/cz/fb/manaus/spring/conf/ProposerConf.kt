package cz.fb.manaus.spring.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.util.logging.Logger
import javax.annotation.PostConstruct

@ConstructorBinding
@ConfigurationProperties("proposer")
data class ProposerConf(
        val downgradeBackProposerBackPrice: Double = 0.0,
        val downgradeBackProposerLayPrice: Double = 0.0,
        val downgradeLayProposerBackPrice: Double = 0.0,
        val downgradeLayProposerLayPrice: Double = 0.0
) {

    private val log = Logger.getLogger(ProposerConf::class.simpleName)

    @PostConstruct
    fun validate() {
        log.info { "$this" }
        check(downgradeBackProposerBackPrice > 0.0)
        check(downgradeLayProposerBackPrice > 0.0)
        check(downgradeBackProposerLayPrice > 0.0)
        check(downgradeLayProposerLayPrice > 0.0)
    }
}
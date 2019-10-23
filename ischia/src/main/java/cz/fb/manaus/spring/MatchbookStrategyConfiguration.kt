package cz.fb.manaus.spring

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.proposer.DowngradeStrategy
import cz.fb.manaus.spring.conf.PriceConf
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("matchbook")
@Configuration
open class MatchbookStrategyConfiguration {

    @Bean
    open fun downgradeStrategy(priceConf: PriceConf): DowngradeStrategy {
        return object : DowngradeStrategy {
            override fun invoke(ctx: BetContext): Double {
                return when (ctx.side) {
                    Side.LAY -> priceConf.downgradeLayRate
                    Side.BACK -> priceConf.downgradeBackRate
                }
            }
        }
    }
}

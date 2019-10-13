package cz.fb.manaus.spring

import cz.fb.manaus.reactor.betting.AmountAdviser
import cz.fb.manaus.reactor.betting.FixedAmountAdviser
import cz.fb.manaus.spring.conf.BettingConf
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(value = ["cz.fb.manaus.reactor"])
@EnableConfigurationProperties(BettingConf::class)
open class ReactorLocalConfiguration {

    @Bean
    open fun fixedAmountAdviser(bettingConf: BettingConf): AmountAdviser {
        val amount = bettingConf.amount
        return FixedAmountAdviser(amount)
    }
}

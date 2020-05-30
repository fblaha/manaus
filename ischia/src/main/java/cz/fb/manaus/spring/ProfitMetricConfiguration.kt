package cz.fb.manaus.spring

import cz.fb.manaus.reactor.profit.metrics.ProfitMetricDescriptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("ischia")
open class ProfitMetricConfiguration {

    @Bean
    open fun dailyTypeProfitMetric(): ProfitMetricDescriptor = ProfitMetricDescriptor(
            interval = "1d",
            categoryPrefix = "market_type",
            categoryValues = setOf("match_odds", "handicap", "total")
    )

    @Bean
    open fun dailySideProfitMetric(): ProfitMetricDescriptor = ProfitMetricDescriptor(
            interval = "1d",
            categoryPrefix = "side",
            categoryValues = setOf("lay", "back")
    )

    @Bean
    open fun weeklyTypeProfitMetric(): ProfitMetricDescriptor = ProfitMetricDescriptor(
            interval = "7d",
            categoryPrefix = "market_type",
            categoryValues = setOf("match_odds", "handicap", "total")
    )

    @Bean
    open fun weeklySideProfitMetric(): ProfitMetricDescriptor = ProfitMetricDescriptor(
            interval = "7d",
            categoryPrefix = "side",
            categoryValues = setOf("lay", "back")
    )

}

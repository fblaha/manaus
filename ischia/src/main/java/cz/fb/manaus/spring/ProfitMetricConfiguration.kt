package cz.fb.manaus.spring

import cz.fb.manaus.reactor.profit.metrics.ProfitMetricSpec
import cz.fb.manaus.reactor.profit.metrics.UpdateFrequency
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("ischia")
open class ProfitMetricConfiguration {

    @Bean
    open fun dailyTypeProfitMetric(): ProfitMetricSpec = ProfitMetricSpec(
            interval = "1d",
            updateFrequency = UpdateFrequency.HIGH,
            categoryPrefix = "market_type",
            categoryValues = setOf("match_odds", "handicap", "total")
    )

    @Bean
    open fun dailySideProfitMetric(): ProfitMetricSpec = ProfitMetricSpec(
            interval = "1d",
            updateFrequency = UpdateFrequency.HIGH,
            categoryPrefix = "side",
            categoryValues = setOf("lay", "back")
    )

    @Bean
    open fun weeklyTypeProfitMetric(): ProfitMetricSpec = ProfitMetricSpec(
            interval = "7d",
            updateFrequency = UpdateFrequency.MEDIUM,
            categoryPrefix = "market_type",
            categoryValues = setOf("match_odds", "handicap", "total")
    )

    @Bean
    open fun weeklySideProfitMetric(): ProfitMetricSpec = ProfitMetricSpec(
            interval = "7d",
            updateFrequency = UpdateFrequency.MEDIUM,
            categoryPrefix = "side",
            categoryValues = setOf("lay", "back")
    )

    @Bean
    open fun monthlyTypeProfitMetric(): ProfitMetricSpec = ProfitMetricSpec(
            interval = "30d",
            updateFrequency = UpdateFrequency.LOW,
            categoryPrefix = "market_type",
            categoryValues = setOf("match_odds", "handicap", "total")
    )

    @Bean
    open fun monthlySideProfitMetric(): ProfitMetricSpec = ProfitMetricSpec(
            interval = "30d",
            updateFrequency = UpdateFrequency.LOW,
            categoryPrefix = "side",
            categoryValues = setOf("lay", "back")
    )
}

package cz.fb.manaus.spring

import cz.fb.manaus.reactor.profit.metrics.ProfitMetricSpec
import cz.fb.manaus.reactor.profit.metrics.ProfitQuery
import cz.fb.manaus.reactor.profit.metrics.UpdateFrequency
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("ischia")
open class ProfitMetricConfiguration {

    private val dailyQuery = ProfitQuery(
            interval = "1d",
            updateFrequency = UpdateFrequency.HIGH
    )

    private val weeklyQuery = ProfitQuery(
            interval = "7d",
            updateFrequency = UpdateFrequency.MEDIUM
    )

    private val monthlyQuery = ProfitQuery(
            interval = "30d",
            updateFrequency = UpdateFrequency.LOW
    )

    private val quarterlyQuery = ProfitQuery(
            interval = "90d",
            updateFrequency = UpdateFrequency.LOW
    )

    @Bean
    open fun dailySideProfitMetric(): ProfitMetricSpec = ProfitMetricSpec(
            query = dailyQuery,
            categoryPrefix = "side",
            categoryValues = setOf("lay", "back")
    )

    @Bean
    open fun weeklySideProfitMetric(): ProfitMetricSpec = ProfitMetricSpec(
            query = weeklyQuery,
            categoryPrefix = "side",
            categoryValues = setOf("lay", "back")
    )

    @Bean
    open fun monthlySideProfitMetric(): ProfitMetricSpec = ProfitMetricSpec(
            query = monthlyQuery,
            categoryPrefix = "side",
            categoryValues = setOf("lay", "back")
    )

    @Bean
    open fun quarterlySideProfitMetric(): ProfitMetricSpec = ProfitMetricSpec(
            query = quarterlyQuery,
            categoryPrefix = "side",
            categoryValues = setOf("lay", "back")
    )
}

package cz.fb.manaus.reactor.profit.metrics

import cz.fb.manaus.reactor.profit.ProfitLoader
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@Profile(ManausProfiles.DB)
class ProfitMetricManager(
        private val profitLoader: ProfitLoader,
        private val specs: List<ProfitMetricSpec> = emptyList()
) {

    @Scheduled(fixedRateString = "PT30M")
    fun computeMetrics() {
        specs.forEach { print("TODO $it") }
    }

}
package cz.fb.manaus.reactor.filter

import cz.fb.manaus.core.maintanance.ConfigUpdate
import cz.fb.manaus.core.maintanance.PeriodicMaintenanceTask
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Duration

@Component
@Profile(ManausProfiles.DB)
class UnprofitableCategoriesRefresher(
        @param:Value("#{systemEnvironment['MNS_UNPROFITABLE_REFRESH_PERIOD_HRS'] ?: 8}") private val refreshPeriodHours: Long,
        private val unprofitableCategoriesRegistries: List<AbstractUnprofitableCategoriesRegistry>) : PeriodicMaintenanceTask {


    override val name: String = "unprofitableCategoriesRefresh"

    override val pausePeriod: Duration = Duration.ofHours(refreshPeriodHours)

    override fun execute(): ConfigUpdate {
        val configUpdate = ConfigUpdate.empty(Duration.ofDays(1))
        for (registry in unprofitableCategoriesRegistries) {
            registry.updateBlacklists(configUpdate)
        }
        return configUpdate
    }

}

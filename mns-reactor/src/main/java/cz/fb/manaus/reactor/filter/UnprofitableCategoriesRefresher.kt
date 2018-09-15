package cz.fb.manaus.reactor.filter

import cz.fb.manaus.core.maintanance.ConfigUpdate
import cz.fb.manaus.core.maintanance.PeriodicMaintenanceTask
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*

@Component
@Profile(ManausProfiles.DB)
class UnprofitableCategoriesRefresher @Autowired
constructor(@param:Value(REFRESH_PERIOD_EL) private val refreshPeriodHours: Int) : PeriodicMaintenanceTask {

    @Autowired(required = false)
    private val unprofitableCategoriesRegistries = LinkedList<AbstractUnprofitableCategoriesRegistry>()

    override fun getName(): String {
        return "unprofitableCategoriesRefresh"
    }

    override fun getPausePeriod(): Duration {
        return Duration.ofHours(8)
    }

    override fun execute(): ConfigUpdate {
        val configUpdate = ConfigUpdate.empty(Duration.ofDays(1))
        for (registry in unprofitableCategoriesRegistries) {
            registry.updateBlacklists(configUpdate)
        }
        return configUpdate
    }

    companion object {
        const val REFRESH_PERIOD_EL = "#{systemEnvironment['MNS_UNPROFITABLE_REFRESH_PERIOD_HRS'] ?: 8}"
    }
}

package cz.fb.manaus.rest

import cz.fb.manaus.core.maintanance.ConfigUpdate
import cz.fb.manaus.core.maintanance.PeriodicMaintenanceTask
import org.springframework.stereotype.Component

import java.time.Duration

@Component
internal class TestTask : PeriodicMaintenanceTask {

    override fun getName(): String {
        return "testTask"
    }

    override fun getPausePeriod(): Duration {
        return Duration.ofNanos(777)
    }

    override fun execute(): ConfigUpdate {
        val command = ConfigUpdate.empty(Duration.ofHours(12))
        command.deletePrefixes.add("test_delete")
        return command
    }
}

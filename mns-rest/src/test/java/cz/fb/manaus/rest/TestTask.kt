package cz.fb.manaus.rest

import cz.fb.manaus.core.maintanance.ConfigUpdate
import cz.fb.manaus.core.maintanance.PeriodicTask
import org.springframework.stereotype.Component

import java.time.Duration

@Component
internal class TestTask : PeriodicTask {

    override val name: String = "testTask"

    override val pausePeriod: Duration = Duration.ofNanos(777)

    override fun execute(): ConfigUpdate {
        val command = ConfigUpdate.empty(Duration.ofHours(12))
        command.deletePrefixes.add("test_delete")
        return command
    }
}

package cz.fb.manaus.core.maintanance

import java.time.Duration

interface PeriodicMaintenanceTask {

    val name: String

    val pausePeriod: Duration

    fun execute(): ConfigUpdate

}

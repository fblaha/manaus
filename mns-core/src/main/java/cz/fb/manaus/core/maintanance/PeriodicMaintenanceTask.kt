package cz.fb.manaus.core.maintanance

import com.fasterxml.jackson.annotation.JsonIgnore

import java.time.Duration

interface PeriodicMaintenanceTask {

    val name: String

    @get:JsonIgnore
    val pausePeriod: Duration

    val pausePeriodNanos: Long
        get() = pausePeriod.toNanos()

    fun execute(): ConfigUpdate

}

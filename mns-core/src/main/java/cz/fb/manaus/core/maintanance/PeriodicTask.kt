package cz.fb.manaus.core.maintanance

import java.time.Duration

interface PeriodicTask {
    val name: String
    val pausePeriod: Duration
    fun execute()
}

package cz.fb.manaus.core.maintanance.db

import cz.fb.manaus.core.model.MarketFootprint
import java.time.Instant

data class TimeRange(val from: Instant?, val to: Instant?)

interface MarketDeletionApprover {

    val timeRange: TimeRange?
        get() = null

    fun isDeletable(marketFootprint: MarketFootprint): Boolean

}

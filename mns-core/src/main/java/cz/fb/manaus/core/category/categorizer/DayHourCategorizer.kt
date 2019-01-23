package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.model.Market
import org.springframework.stereotype.Component
import java.util.*

@Component
object DayHourCategorizer : AbstractDelegatingCategorizer("dayHour_") {

    private val ranges: List<ClosedRange<Int>> = listOf(
            0..3, 4..7, 8..11, 12..15, 16..19, 20..23
    )

    public override fun getCategoryRaw(market: Market): Set<String> {
        val startTime = Calendar.getInstance()
        startTime.time = Date.from(market.event.openDate)
        val hour = startTime.get(Calendar.HOUR_OF_DAY)
        val range = ranges.first { hour in it }
        return setOf(range.start.toString() + "_" + range.endInclusive)
    }
}

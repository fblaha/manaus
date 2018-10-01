package cz.fb.manaus.core.category.categorizer

import com.google.common.collect.ImmutableList.of
import com.google.common.collect.Range
import com.google.common.collect.Range.closedOpen
import cz.fb.manaus.core.model.Market
import org.springframework.stereotype.Component
import java.util.*

@Component
class DayHourCategorizer : AbstractDelegatingCategorizer("dayHour_") {

    public override fun getCategoryRaw(market: Market): Set<String> {
        val startTime = Calendar.getInstance()
        startTime.time = market.event.openDate
        val hour = startTime.get(Calendar.HOUR_OF_DAY)
        for (range in RANGES) {
            if (range.contains(hour)) {
                return setOf(range.lowerEndpoint().toString() + "_" + range.upperEndpoint())
            }
        }
        throw IllegalStateException()

    }

    companion object {
        val RANGES: List<Range<Int>> = of(
                closedOpen(0, 4),
                closedOpen(4, 8),
                closedOpen(8, 12),
                closedOpen(12, 16),
                closedOpen(16, 20),
                closedOpen(20, 24))
    }
}

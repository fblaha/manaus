package cz.fb.manaus.rest

import com.google.common.base.CharMatcher
import com.google.common.base.Splitter
import com.google.common.collect.Range
import org.springframework.stereotype.Component

import java.time.Instant
import java.time.temporal.ChronoUnit

@Component
class IntervalParser {

    internal fun parse(date: Instant, interval: String): Range<Instant> {
        var shiftedDate = date
        val split = Splitter.on('-').splitToList(interval)
        val intervalOnly = split[0]

        val count = Integer.parseInt(CharMatcher.digit().retainFrom(intervalOnly))
        val unitChar = CharMatcher.digit().removeFrom(intervalOnly)[0]
        val unit = UNITS[unitChar]

        if (split.size == 2) {
            val offsetDays = Integer.parseInt(split[1])
            shiftedDate = shiftedDate.minus(offsetDays.toLong(), unit)
        }

        return Range.closed(shiftedDate.minus(count.toLong(), unit), shiftedDate)
    }

    companion object {
        const val INTERVAL = "{interval:\\d+[hmd](?:-\\d+)?}"
        var UNITS = mapOf(
                'h' to ChronoUnit.HOURS,
                'm' to ChronoUnit.MINUTES,
                'd' to ChronoUnit.DAYS)
    }
}

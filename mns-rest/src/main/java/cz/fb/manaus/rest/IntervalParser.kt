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
        var date = date
        var interval = interval
        val split = Splitter.on('-').splitToList(interval)
        interval = split[0]

        val count = Integer.parseInt(CharMatcher.digit().retainFrom(interval))
        val unitChar = CharMatcher.digit().removeFrom(interval)[0]
        val unit = UNITS[unitChar]

        if (split.size == 2) {
            val offsetDays = Integer.parseInt(split[1])
            date = date.minus(offsetDays.toLong(), unit)
        }

        return Range.closed(date.minus(count.toLong(), unit), date)
    }

    companion object {
        const val INTERVAL = "{interval:\\d+[hmd](?:-\\d+)?}"
        var UNITS = mapOf(
                'h' to ChronoUnit.HOURS,
                'm' to ChronoUnit.MINUTES,
                'd' to ChronoUnit.DAYS)
    }
}

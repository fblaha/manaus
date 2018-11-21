package cz.fb.manaus.rest

import com.google.common.base.CharMatcher
import com.google.common.base.Splitter
import cz.fb.manaus.core.maintanance.db.TimeRange
import java.time.Instant
import java.time.temporal.ChronoUnit

object IntervalParser {

    const val INTERVAL = "{interval:\\d+[hmd](?:-\\d+)?}"
    private var units = mapOf(
            'h' to ChronoUnit.HOURS,
            'm' to ChronoUnit.MINUTES,
            'd' to ChronoUnit.DAYS)

    internal fun parse(date: Instant, interval: String): TimeRange {
        var shiftedDate = date
        val split = Splitter.on('-').splitToList(interval)
        val intervalOnly = split[0]

        val count = Integer.parseInt(CharMatcher.digit().retainFrom(intervalOnly))
        val unitChar = CharMatcher.digit().removeFrom(intervalOnly)[0]
        val unit = units[unitChar]

        if (split.size == 2) {
            val offsetDays = Integer.parseInt(split[1])
            shiftedDate = shiftedDate.minus(offsetDays.toLong(), unit)
        }

        return TimeRange(shiftedDate.minus(count.toLong(), unit), shiftedDate)
    }

}

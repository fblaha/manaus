package cz.fb.manaus.core.time

import cz.fb.manaus.core.maintanance.db.TimeRange
import java.time.Instant
import java.time.temporal.ChronoUnit

object IntervalParser {

    const val INTERVAL = "{interval:\\d+[hmd](?:-\\d+)?}"
    private var units = mapOf(
            'h' to ChronoUnit.HOURS,
            'm' to ChronoUnit.MINUTES,
            'd' to ChronoUnit.DAYS
    )

    fun parse(date: Instant, interval: String): TimeRange {
        var shiftedDate = date
        val split = interval.split('-')
        val intervalOnly = split[0]

        val count = Integer.parseInt(intervalOnly.filter { it.isDigit() })
        val unitChar = intervalOnly.filter { it.isLetter() }[0]
        val unit = units[unitChar]

        if (split.size == 2) {
            val offsetDays = Integer.parseInt(split[1])
            shiftedDate = shiftedDate.minus(offsetDays.toLong(), unit)
        }

        return TimeRange(shiftedDate.minus(count.toLong(), unit), shiftedDate)
    }

}

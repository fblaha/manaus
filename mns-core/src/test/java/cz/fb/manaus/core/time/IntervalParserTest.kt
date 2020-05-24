package cz.fb.manaus.core.time

import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals

class IntervalParserTest {

    @Test
    fun subtract() {
        val now = Instant.now()
        assertEquals(now.minus(2, ChronoUnit.HOURS), IntervalParser.parse(now, "2h").from)
        assertEquals(now.minus(10, ChronoUnit.DAYS), IntervalParser.parse(now, "10d").from)
        assertEquals(now.minus(5, ChronoUnit.MINUTES), IntervalParser.parse(now, "5m").from)
    }

    @Test
    fun `offset lower endpoint`() {
        val now = Instant.now()
        assertEquals(now.minus(6, ChronoUnit.HOURS), IntervalParser.parse(now, "2h-4").from)
        assertEquals(now.minus(12, ChronoUnit.DAYS), IntervalParser.parse(now, "10d-2").from)
    }

    @Test
    fun `offset upper endpoint`() {
        val now = Instant.now()
        assertEquals(now.minus(4, ChronoUnit.HOURS), IntervalParser.parse(now, "2h-4").to)
        assertEquals(now.minus(2, ChronoUnit.DAYS), IntervalParser.parse(now, "10d-2").to)
    }
}

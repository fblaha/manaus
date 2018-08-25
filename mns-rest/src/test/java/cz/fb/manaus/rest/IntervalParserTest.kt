package cz.fb.manaus.rest

import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals

class IntervalParserTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var intervalParser: IntervalParser

    @Test
    fun subtract() {
        val now = Instant.now()
        assertEquals(now.minus(2, ChronoUnit.HOURS), intervalParser.parse(now, "2h").lowerEndpoint())
        assertEquals(now.minus(10, ChronoUnit.DAYS), intervalParser.parse(now, "10d").lowerEndpoint())
        assertEquals(now.minus(5, ChronoUnit.MINUTES), intervalParser.parse(now, "5m").lowerEndpoint())
    }

    @Test
    fun `offset lower endpoint`() {
        val now = Instant.now()
        assertEquals(now.minus(6, ChronoUnit.HOURS), intervalParser.parse(now, "2h-4").lowerEndpoint())
        assertEquals(now.minus(12, ChronoUnit.DAYS), intervalParser.parse(now, "10d-2").lowerEndpoint())
    }

    @Test
    fun `offset upper endpoint`() {
        val now = Instant.now()
        assertEquals(now.minus(4, ChronoUnit.HOURS), intervalParser.parse(now, "2h-4").upperEndpoint())
        assertEquals(now.minus(2, ChronoUnit.DAYS), intervalParser.parse(now, "10d-2").upperEndpoint())
    }
}

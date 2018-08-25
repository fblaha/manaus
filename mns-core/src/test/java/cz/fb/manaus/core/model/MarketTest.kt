package cz.fb.manaus.core.model

import com.fasterxml.jackson.databind.ObjectMapper
import cz.fb.manaus.core.test.CoreTestFactory
import org.junit.Test
import kotlin.test.assertEquals


class MarketTest {

    @Test
    fun testDoubleSerialization() {
        var market = CoreTestFactory.newTestMarket()
        val mapper = ObjectMapper()
        val serialized = mapper.writer().writeValueAsString(market)
        market = mapper.readerFor(Market::class.java).readValue(serialized)
        val doubleSerialized = mapper.writer().writeValueAsString(market)
        assertEquals(serialized, doubleSerialized)
    }

}

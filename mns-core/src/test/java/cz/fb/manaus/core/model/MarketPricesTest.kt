package cz.fb.manaus.core.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.ImmutableList.of
import cz.fb.manaus.core.model.MarketPrices.getOverround
import cz.fb.manaus.core.test.CoreTestFactory
import cz.fb.manaus.core.test.CoreTestFactory.Companion.newTestMarket
import org.apache.commons.math3.util.Precision
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MarketPricesTest {

    @Test
    fun `json marshall`() {
        val prices = CoreTestFactory.newMarketPrices(1, 2.4)
        val mapper = ObjectMapper()
        val serialized = mapper.writer().writeValueAsString(prices)
        val restored = mapper.readerFor(MarketPrices::class.java).readValue<MarketPrices>(serialized)
        val doubleSerialized = mapper.writer().writeValueAsString(restored)
        assertEquals(serialized, doubleSerialized)
    }

    @Test
    fun `reciprocal`() {
        assertEquals(0.8, CoreTestFactory.newMarketPrices(1, 2.4).getReciprocal(Side.BACK).asDouble)
        assertEquals(1.0, CoreTestFactory.newMarketPrices(1, 3.0).getReciprocal(Side.BACK).asDouble)
    }

    @Test
    fun `reciprocal 2 winners`() {
        assertEquals(1.0, CoreTestFactory.newMarketPrices(2, 1.5).getReciprocal(Side.BACK).asDouble)
    }

    @Test
    fun `last matched reciprocal`() {
        assertEquals(1.0, CoreTestFactory.newTestMarketPrices(newTestMarket()).lastMatchedReciprocal.asDouble)
    }

    @Test
    fun `reciprocal 2 winners compare`() {
        assertTrue(CoreTestFactory.newMarketPrices(2, 1.45).getReciprocal(Side.BACK).asDouble < CoreTestFactory.newMarketPrices(2, 1.46).getReciprocal(Side.BACK).asDouble)
    }

    @Test
    fun `overround`() {
        val overround = getOverround(of(2.5, 3.25, 3.0))
        assertEquals(1.041, Precision.round(overround, 3))
    }
}

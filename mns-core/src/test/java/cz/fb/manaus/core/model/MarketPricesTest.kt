package cz.fb.manaus.core.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.ImmutableList.of
import cz.fb.manaus.core.model.MarketPrices.getOverround
import cz.fb.manaus.core.test.CoreTestFactory
import org.apache.commons.math3.util.Precision
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Test

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
        assertThat(CoreTestFactory.newMarketPrices(1, 2.4).getReciprocal(Side.BACK).asDouble, `is`(0.8))
        assertThat(CoreTestFactory.newMarketPrices(1, 3.0).getReciprocal(Side.BACK).asDouble, `is`(1.0))
    }

    @Test
    fun `reciprocal 2 winners`() {
        assertThat(CoreTestFactory.newMarketPrices(2, 1.5).getReciprocal(Side.BACK).asDouble, `is`(1.0))
    }

    @Test
    fun `last matched reciprocal`() {
        assertThat(CoreTestFactory.newMarketPrices(null).lastMatchedReciprocal.asDouble, `is`(1.0))
    }

    @Test
    fun `reciprocal 2 winners compare`() {
        assertTrue(CoreTestFactory.newMarketPrices(2, 1.45).getReciprocal(Side.BACK).asDouble < CoreTestFactory.newMarketPrices(2, 1.46).getReciprocal(Side.BACK).asDouble)
    }

    @Test
    fun `overround`() {
        val overround = getOverround(of(2.5, 3.25, 3.0))
        assertThat(Precision.round(overround, 3), `is`(1.041))
    }
}

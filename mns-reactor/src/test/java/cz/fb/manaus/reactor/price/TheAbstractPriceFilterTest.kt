package cz.fb.manaus.reactor.price

import com.google.common.collect.Range
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.ReactorTestFactory
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.test.assertEquals


class TheAbstractPriceFilterTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var filter: TestFilter
    @Autowired
    private lateinit var testFactory: ReactorTestFactory

    @Test
    fun `significant prices size`() {
        assertEquals(2, filter.getSignificantPrices(1, SAMPLE_PRICES).size)
        assertEquals(4, filter.getSignificantPrices(2, SAMPLE_PRICES).size)
        assertEquals(6, filter.getSignificantPrices(3, SAMPLE_PRICES).size)
        assertEquals(8, filter.getSignificantPrices(4, SAMPLE_PRICES).size)
        assertEquals(9, filter.getSignificantPrices(5, SAMPLE_PRICES).size)
        assertEquals(10, filter.getSignificantPrices(6, SAMPLE_PRICES).size)
    }

    @Test
    fun `significant prices`() {
        assertEquals(listOf(BACK1, LAY1), filter.getSignificantPrices(1, SAMPLE_PRICES))
        assertEquals(listOf(BACK1, BACK2, LAY1, LAY2), filter.getSignificantPrices(2, SAMPLE_PRICES))
        assertEquals(listOf(BACK1, BACK2, BACK3, LAY1, LAY2, LAY3), filter.getSignificantPrices(3, SAMPLE_PRICES))
    }


    @Test
    fun `best prices`() {
        val market = testFactory.createMarketPrices(0.15, listOf(0.5, 0.3, 0.2))
        for (runnerPrices in market) {
            val bestBack = runnerPrices.getHomogeneous(Side.BACK).bestPrice!!
            val bestLay = runnerPrices.getHomogeneous(Side.LAY).bestPrice!!
            val prices = runnerPrices.prices.toList()
            val filteredPrices = this.filter.filter(prices)
            val bySide = filteredPrices.map { it.side to it }.toMap()

            assertEquals(bestBack, bySide[Side.BACK])
            assertEquals(bestLay, bySide[Side.LAY])
        }
    }

    @Component
    private class TestFilter : AbstractPriceFilter(1, -1.0, Range.all())

    companion object {
        val BACK1 = Price(1.96, 5.0, Side.BACK)
        val LAY1 = Price(2.04, 5.0, Side.LAY)
        val BACK2 = Price(1.92, 5.0, Side.BACK)
        val LAY2 = Price(2.1, 5.0, Side.LAY)
        val BACK3 = Price(1.90, 5.0, Side.BACK)
        val LAY3 = Price(2.15, 5.0, Side.LAY)
        val SAMPLE_PRICES = listOf(LAY1, LAY2,
                LAY3, Price(2.2, 5.0, Side.LAY),
                Price(1.8, 5.0, Side.BACK), Price(1.82, 5.0, Side.BACK),
                BACK2, Price(1.88, 5.0, Side.BACK),
                BACK3, BACK1)
    }
}
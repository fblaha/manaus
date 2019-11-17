package cz.fb.manaus.reactor.price

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.provider
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.PricesTestFactory
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals


class PriceFilterTest : AbstractLocalTestCase() {

    private lateinit var filter: PriceFilter
    @Autowired
    private lateinit var testFactory: PricesTestFactory
    @Autowired
    private lateinit var priceBulldozer: PriceBulldozer

    @Before
    fun setUp() {
        filter = PriceFilter(1, -1.0, 0.0..100.0, priceBulldozer)
    }

    private val back1 = Price(1.96, 5.0, Side.BACK)
    private val lay1 = Price(2.04, 5.0, Side.LAY)
    private val back2 = Price(1.92, 5.0, Side.BACK)
    private val lay2 = Price(2.1, 5.0, Side.LAY)
    private val back3 = Price(1.90, 5.0, Side.BACK)
    private val lay3 = Price(2.15, 5.0, Side.LAY)
    private val samplePrices = listOf(
            lay1,
            lay2,
            lay3,
            Price(2.2, 5.0, Side.LAY),
            Price(1.8, 5.0, Side.BACK),
            Price(1.82, 5.0, Side.BACK),
            back2,
            Price(1.88, 5.0, Side.BACK),
            back3,
            back1
    )

    @Test
    fun `significant prices size`() {
        assertEquals(2, filter.getSignificantPrices(1, samplePrices, provider::matches).size)
        assertEquals(4, filter.getSignificantPrices(2, samplePrices, provider::matches).size)
        assertEquals(6, filter.getSignificantPrices(3, samplePrices, provider::matches).size)
        assertEquals(8, filter.getSignificantPrices(4, samplePrices, provider::matches).size)
        assertEquals(9, filter.getSignificantPrices(5, samplePrices, provider::matches).size)
        assertEquals(10, filter.getSignificantPrices(6, samplePrices, provider::matches).size)
    }

    @Test
    fun `significant prices`() {
        assertEquals(listOf(back1, lay1), filter.getSignificantPrices(1, samplePrices, provider::matches))
        assertEquals(listOf(back1, back2, lay1, lay2),
                filter.getSignificantPrices(2, samplePrices, provider::matches))
        assertEquals(listOf(back1, back2, back3, lay1, lay2, lay3),
                filter.getSignificantPrices(3, samplePrices, provider::matches))
    }

    @Test
    fun `best prices`() {
        val market = testFactory.newMarketPrices(0.15, listOf(0.5, 0.3, 0.2))
        for (runnerPrices in market) {
            val bestBack = runnerPrices.getHomogeneous(Side.BACK).bestPrice!!
            val bestLay = runnerPrices.getHomogeneous(Side.LAY).bestPrice!!
            val prices = runnerPrices.prices.toList()
            val filteredPrices = filter.filter(prices, provider::matches)
            val bySide = filteredPrices.map { it.side to it }.toMap()

            assertEquals(bestBack, bySide[Side.BACK])
            assertEquals(bestLay, bySide[Side.LAY])
        }
    }

}
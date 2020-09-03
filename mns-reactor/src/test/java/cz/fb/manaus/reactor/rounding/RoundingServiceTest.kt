package cz.fb.manaus.reactor.rounding

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.bfProvider
import cz.fb.manaus.core.provider.ProviderMatcher
import cz.fb.manaus.core.test.AbstractTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals


class RoundingServiceTest : AbstractTestCase() {
    private val start = 1.01
    private val repeat = 200

    @Autowired
    private lateinit var service: RoundingService

    @Test
    fun `increment decrement`() {
        var price = start
        val upList = mutableListOf<Double>()
        val downList = mutableListOf<Double>()
        for (i in 0 until repeat) {
            price = service.increment(price, 1, bfProvider::matches)!!
            upList.add(price)
        }
        price = service.increment(price, 1, bfProvider::matches)!!
        for (i in 0 until repeat) {
            price = service.decrement(price, 1, bfProvider.minPrice, bfProvider::matches)!!
            downList.add(price)
        }
        price = service.decrement(price, 1, bfProvider.minPrice, bfProvider::matches)!!
        assertEquals(start, price)
        downList.reverse()
        assertEquals(downList, upList)
    }

    @Test
    fun steps() {
        var price1 = start
        for (i in 0 until repeat) {
            price1 = service.increment(price1, 1, bfProvider::matches)!!
        }
        var price2 = start
        for (i in 0 until repeat / 50) {
            price2 = service.increment(price2, 50, bfProvider::matches)!!
        }
        assertEquals(price1, price2)

        for (i in 0 until repeat) {
            price1 = service.decrement(price1, 1, bfProvider.minPrice, bfProvider::matches)!!
        }
        for (i in 0 until repeat / 50) {
            price2 = service.decrement(price2, 50, bfProvider.minPrice, bfProvider::matches)!!
        }
        assertEquals(price1, price2)
    }

    @Test
    fun `round bet`() {
        assertEquals(1.05, service.roundBet(1.0544444444, bfProvider::matches))
        assertEquals(1.06, service.roundBet(1.05555555, bfProvider::matches))
        assertEquals(2.08, service.roundBet(2.081, bfProvider::matches))
        assertEquals(2.1, service.roundBet(2.09, bfProvider::matches))
        assertEquals(3.6, service.roundBet(3.575, bfProvider::matches))
        assertEquals(3.85, service.roundBet(3.84, bfProvider::matches))
        assertEquals(3.80, service.roundBet(3.81, bfProvider::matches))
        assertEquals(3.85, service.roundBet(3.83, bfProvider::matches))
        assertEquals(5.2, service.roundBet(5.15, bfProvider::matches))
        assertEquals(5.1, service.roundBet(5.14, bfProvider::matches))
        assertEquals(8.8, service.roundBet(8.7, bfProvider::matches))
        assertEquals(8.6, service.roundBet(8.69, bfProvider::matches))
        assertEquals(980.0, service.roundBet(984.0, bfProvider::matches))
        assertEquals(990.0, service.roundBet(985.0, bfProvider::matches))
    }

    @Test
    fun `round bet - loop test`() {
        var price = start
        for (i in 0 until repeat) {
            price = service.increment(price, 1, bfProvider::matches)!!
            val oneMore = service.increment(price, 1, bfProvider::matches)!!
            val step = oneMore - price
            assertEquals(price, service.roundBet(price, bfProvider::matches)!!)
            assertEquals(price, service.roundBet(price + step / 4.0, bfProvider::matches)!!)
            assertEquals(oneMore, service.roundBet(price + 3.0 * step / 4.0, bfProvider::matches)!!)
        }
    }
}

fun RoundingService.increment(price: Price, stepNum: Int, providerMatcher: ProviderMatcher): Price? {
    val newPrice = increment(price.price, stepNum, providerMatcher)
    return newPrice?.let { Price(it, price.amount, price.side) }
}

fun RoundingService.decrement(price: Price, stepNum: Int, minPrice: Double, providerMatcher: ProviderMatcher): Price? {
    val newPrice = decrement(price.price, stepNum, minPrice, providerMatcher)
    return newPrice?.let { Price(it, price.amount, price.side) }
}

package cz.fb.manaus.reactor.rounding

import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.test.assertEquals


class RoundingServiceTest : AbstractLocalTestCase() {
    @Autowired
    private lateinit var service: RoundingService

    @Test
    fun `increment decrement`() {
        var price = START
        val upList = LinkedList<Double>()
        val downList = LinkedList<Double>()
        for (i in 0 until REPEAT) {
            price = service.increment(price, 1).asDouble
            upList.add(price)
        }
        price = service.increment(price, 1).asDouble
        for (i in 0 until REPEAT) {
            price = service.decrement(price, 1).asDouble
            downList.add(price)
        }
        price = service.decrement(price, 1).asDouble
        assertEquals(START, price)
        downList.reverse()
        assertEquals(downList, upList)
    }

    @Test
    fun steps() {
        var price1 = START
        for (i in 0 until REPEAT) {
            price1 = service.increment(price1, 1).asDouble
        }
        var price2 = START
        for (i in 0 until REPEAT / 50) {
            price2 = service.increment(price2, 50).asDouble
        }
        assertEquals(price1, price2)

        for (i in 0 until REPEAT) {
            price1 = service.decrement(price1, 1).asDouble
        }
        for (i in 0 until REPEAT / 50) {
            price2 = service.decrement(price2, 50).asDouble
        }
        assertEquals(price1, price2)
    }

    @Test
    fun `round bet`() {
        assertEquals(1.05, service.roundBet(1.0544444444).asDouble)
        assertEquals(1.06, service.roundBet(1.05555555).asDouble)
        assertEquals(2.08, service.roundBet(2.081).asDouble)
        assertEquals(2.1, service.roundBet(2.09).asDouble)
        assertEquals(3.6, service.roundBet(3.575).asDouble)
        assertEquals(3.85, service.roundBet(3.84).asDouble)
        assertEquals(3.80, service.roundBet(3.81).asDouble)
        assertEquals(3.85, service.roundBet(3.83).asDouble)
        assertEquals(5.2, service.roundBet(5.15).asDouble)
        assertEquals(5.1, service.roundBet(5.14).asDouble)
        assertEquals(8.8, service.roundBet(8.7).asDouble)
        assertEquals(8.6, service.roundBet(8.69).asDouble)
        assertEquals(980.0, service.roundBet(984.0).asDouble)
        assertEquals(990.0, service.roundBet(985.0).asDouble)
    }

    @Test
    fun `round bet - loop test`() {
        var price = START
        for (i in 0 until REPEAT) {
            price = service.increment(price, 1).asDouble
            val oneMore = service.increment(price, 1).asDouble
            val step = oneMore - price
            assertEquals(price, service.roundBet(price).asDouble)
            assertEquals(price, service.roundBet(price + step / 4.0).asDouble)
            assertEquals(oneMore, service.roundBet(price + 3.0 * step / 4.0).asDouble)
        }
    }

    companion object {
        const val START = 1.01
        const val REPEAT = 200
    }
}

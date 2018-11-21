package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.homeSettledBet
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import org.junit.Test
import kotlin.test.assertEquals

class SettledBetRepositoryTest : AbstractDatabaseTestCase() {

    @Test
    fun `save - read`() {
        settledBetRepository.save(homeSettledBet)
        assertEquals(homeSettledBet, settledBetRepository.read(homeSettledBet.id))
    }

    @Test
    fun `find - side`() {
        settledBetRepository.save(homeSettledBet)
        settledBetRepository.save(homeSettledBet.copy(id = "2", price = Price(3.0, 3.0, Side.LAY)))
        assertEquals(2, settledBetRepository.find().size)
        assertEquals(1, settledBetRepository.find(side = Side.LAY).size)
        assertEquals(1, settledBetRepository.find(side = Side.BACK).size)
    }

    @Test
    fun `find - limit`() {
        settledBetRepository.save(homeSettledBet)
        settledBetRepository.save(homeSettledBet.copy(id = "2"))
        assertEquals(2, settledBetRepository.find().size)
        assertEquals(1, settledBetRepository.find(maxResults = 1).size)
    }

    @Test
    fun `find - from`() {
        val settledTime = homeSettledBet.settled
        settledBetRepository.save(homeSettledBet)
        assertEquals(1, settledBetRepository.find().size)
        assertEquals(0, settledBetRepository.find(from = settledTime.plusSeconds(30)).size)
        assertEquals(1, settledBetRepository.find(from = settledTime.minusSeconds(30)).size)
    }

    @Test
    fun `find - to`() {
        val settledTime = homeSettledBet.settled
        settledBetRepository.save(homeSettledBet)
        val minus30 = settledTime.minusSeconds(30)
        val plus30 = settledTime.plusSeconds(30)
        assertEquals(1, settledBetRepository.find(from = minus30, to = plus30).size)
        assertEquals(0, settledBetRepository.find(from = minus30, to = minus30).size)
    }
}
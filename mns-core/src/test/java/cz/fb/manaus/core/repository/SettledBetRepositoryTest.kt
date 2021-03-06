package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.homeSettledBet
import cz.fb.manaus.core.test.AbstractIntegrationTestCase
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SettledBetRepositoryTest : AbstractIntegrationTestCase() {

    @Test
    fun `save - read`() {
        settledBetRepository.save(homeSettledBet)
        val stored = settledBetRepository.read(homeSettledBet.id) ?: error("missing")
        assertEquals(homeSettledBet, stored)
    }

    @Test
    fun `save - update`() {
        settledBetRepository.save(homeSettledBet)
        assertNull(settledBetRepository.read(homeSettledBet.id)!!.commission)
        settledBetRepository.save(homeSettledBet.copy(commission = 0.1))
        assertEquals(0.1, settledBetRepository.read(homeSettledBet.id)!!.commission)
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
package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.settledBet
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import org.dizitart.no2.objects.filters.ObjectFilters
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class SettledBetRepositoryTest : AbstractDatabaseTestCase() {

    @Autowired
    private lateinit var settledBetRepository: SettledBetRepository


    @Before
    fun setUp() {
        settledBetRepository.repository.remove(ObjectFilters.ALL)
    }

    @Test
    fun `save - read`() {
        settledBetRepository.save(settledBet)
        assertEquals(settledBet, settledBetRepository.read(settledBet.id))
    }

    @Test
    fun `find - side`() {
        settledBetRepository.save(settledBet)
        settledBetRepository.save(settledBet.copy(id = "2", price = Price(3.0, 3.0, Side.LAY)))
        assertEquals(2, settledBetRepository.find().size)
        assertEquals(1, settledBetRepository.find(side = Side.LAY).size)
        assertEquals(1, settledBetRepository.find(side = Side.BACK).size)
    }

    @Test
    fun `find - limit`() {
        settledBetRepository.save(settledBet)
        settledBetRepository.save(settledBet.copy(id = "2"))
        assertEquals(2, settledBetRepository.find().size)
        assertEquals(1, settledBetRepository.find(maxResults = 1).size)
    }

    @Test
    fun `find - from`() {
        val settledTime = settledBet.settled
        settledBetRepository.save(settledBet)
        assertEquals(1, settledBetRepository.find().size)
        assertEquals(0, settledBetRepository.find(from = settledTime.plusSeconds(30)).size)
        assertEquals(1, settledBetRepository.find(from = settledTime.minusSeconds(30)).size)
    }

    @Test
    fun `find - to`() {
        val settledTime = settledBet.settled
        settledBetRepository.save(settledBet)
        val minus30 = settledTime.minusSeconds(30)
        val plus30 = settledTime.plusSeconds(30)
        assertEquals(1, settledBetRepository.find(from = minus30, to = plus30).size)
        assertEquals(0, settledBetRepository.find(from = minus30, to = minus30).size)
    }
}
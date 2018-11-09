package cz.fb.manaus.repository

import org.dizitart.kno2.nitrite
import org.junit.Before
import org.junit.Test
import java.time.Instant
import kotlin.test.assertEquals


val settledBet: SettledBet = SettledBet(
        id = "1",
        selectionId = 100,
        selectionName = "Banik",
        profitAndLoss = 5.0,
        matched = Instant.now(),
        placed = Instant.now(),
        settled = Instant.now(),
        price = Price(3.0, 3.0, Side.BACK)
)

class SettledBetRepositoryTest {

    private lateinit var settledBetRepository: SettledBetRepository

    @Before
    fun setUp() {
        val db = nitrite {
            autoCommitBufferSize = 2048
            autoCompact = false
        }
        settledBetRepository = SettledBetRepository(db)
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
        settledBetRepository.save(settledBet)
        assertEquals(1, settledBetRepository.find().size)
        assertEquals(0, settledBetRepository.find(from = Instant.now().plusSeconds(30)).size)
        assertEquals(1, settledBetRepository.find(from = Instant.now().minusSeconds(30)).size)
    }

    @Test
    fun `find - to`() {
        settledBetRepository.save(settledBet)
        val minus30 = Instant.now().minusSeconds(30)
        val plus30 = Instant.now().plusSeconds(30)
        assertEquals(1, settledBetRepository.find(from = minus30, to = plus30).size)
        assertEquals(0, settledBetRepository.find(from = minus30, to = minus30).size)
    }
}
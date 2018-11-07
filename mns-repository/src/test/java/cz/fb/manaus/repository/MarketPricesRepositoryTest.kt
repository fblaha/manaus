package cz.fb.manaus.repository

import cz.fb.manaus.core.persistence.MarketPrices
import cz.fb.manaus.core.persistence.Price
import cz.fb.manaus.core.persistence.RunnerPrices
import cz.fb.manaus.core.persistence.Side
import org.dizitart.kno2.nitrite
import org.junit.Before
import org.junit.Test
import java.time.Instant
import kotlin.test.assertEquals

val marketPrices = MarketPrices(
        id = 5,
        marketID = "2",
        time = Instant.now(),
        winnerCount = 1,
        runnerPrices = listOf(
                RunnerPrices(
                        selectionId = 100,
                        matchedAmount = 100.0,
                        lastMatchedPrice = 3.0,
                        prices = listOf(
                                Price(3.0, 100.0, Side.BACK),
                                Price(3.5, 100.0, Side.LAY)
                        )
                )
        )
)

class MarketPricesRepositoryTest {

    private lateinit var repository: MarketPricesRepository

    @Before
    fun setUp() {
        val db = nitrite {
            autoCommitBufferSize = 2048
            autoCompact = false
        }
        repository = MarketPricesRepository(db)
    }

    @Test
    fun save() {
        repository.save(marketPrices)
    }

    @Test
    fun find() {
        repository.save(marketPrices)
        assertEquals(1, repository.find("2").size)
        assertEquals(0, repository.find("3").size)
    }

    @Test
    fun delete() {
        repository.save(marketPrices)
        assertEquals(1, repository.find("2").size)
        assertEquals(1, repository.delete("2"))
        assertEquals(0, repository.find("2").size)
    }
}
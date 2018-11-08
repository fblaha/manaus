package cz.fb.manaus.repository

import cz.fb.manaus.core.persistence.*
import org.dizitart.kno2.nitrite
import org.junit.Before
import org.junit.Test
import java.time.Instant
import kotlin.test.assertEquals


val runnerPrices = listOf(
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

val betAction = BetAction(
        id = 5,
        marketID = "2",
        time = Instant.now(),
        selectionID = 1000,
        betID = null,
        betActionType = BetActionType.PLACE,
        runnerPrices = runnerPrices,
        price = Price(3.0, 3.0, Side.BACK),
        properties = mapOf("x" to "y")
)

class BetActionRepositoryTest {

    private lateinit var repository: BetActionRepository

    @Before
    fun setUp() {
        val db = nitrite {
            autoCommitBufferSize = 2048
            autoCompact = false
        }
        repository = BetActionRepository(db)
    }

    @Test
    fun save() {
        repository.save(betAction)
    }

    @Test
    fun find() {
        repository.save(betAction)
        assertEquals(1, repository.find("2").size)
        assertEquals(0, repository.find("3").size)
    }

    @Test
    fun delete() {
        repository.save(betAction)
        assertEquals(1, repository.find("2").size)
        assertEquals(1, repository.delete("2"))
        assertEquals(0, repository.find("2").size)
    }
}
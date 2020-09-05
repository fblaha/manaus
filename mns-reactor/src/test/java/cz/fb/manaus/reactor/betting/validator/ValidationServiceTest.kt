package cz.fb.manaus.reactor.betting.validator

import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractTestCase
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.HOME_EVENT_BACK
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidationServiceTest : AbstractTestCase() {
    @Autowired
    private lateinit var service: ValidationService

    @Test
    fun `reduce OK - NOP - DROP combinations`() {
        val results = listOf(
                ValidationResult.OK,
                ValidationResult.NOP,
                ValidationResult.DROP,
                ValidationResult.OK
        )
        assertEquals(
                ValidationResult.DROP,
                service.reduce(results)
        )
        assertEquals(ValidationResult.OK, service.reduce(results.filter { it == ValidationResult.OK }))
        assertEquals(ValidationResult.NOP, service.reduce(results.filter { it != ValidationResult.DROP }))
    }

    @Test
    fun `downgrade accepting by default`() {
        assertTrue(MockValidator(ValidationResult.DROP).isDowngradeAccepting)
    }

    @Test
    fun `downgrade price`() {
        checkDownGrade(2.0, true)
    }

    @Test
    fun `upgrade price`() {
        checkDownGrade(2.4, false)
    }

    private fun checkDownGrade(newPrice: Double, expected: Boolean) {
        val oldBet = Bet(
                marketId = "1", selectionId = 1, requestedPrice = Price(2.2, 2.0, Side.LAY),
                placedDate = Instant.now(), matchedAmount = 5.0
        )
        val dropping = MockValidator(ValidationResult.DROP)
        val result = service.isDowngrade(Price(newPrice, 2.0, Side.LAY), oldBet)
        assertEquals(expected, result)
        assertEquals(ValidationResult.DROP, dropping.validate(HOME_EVENT_BACK))
    }


    private data class MockValidator(private val result: ValidationResult) : Validator {

        override fun validate(event: BetEvent): ValidationResult {
            return result
        }

    }
}

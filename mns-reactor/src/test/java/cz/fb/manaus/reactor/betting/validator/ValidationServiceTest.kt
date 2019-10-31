package cz.fb.manaus.reactor.betting.validator

import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.HOME_EVENT
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidationServiceTest : AbstractLocalTestCase() {
    @Autowired
    private lateinit var service: ValidationService

    @Test
    fun `reduce ACCEPT - NOP - REJECT combinations`() {
        assertEquals(ValidationResult.REJECT, service.reduce(listOf(
                ValidationResult.ACCEPT,
                ValidationResult.NOP,
                ValidationResult.REJECT,
                ValidationResult.ACCEPT
        )))
        assertEquals(ValidationResult.ACCEPT, service.reduce(listOf(ValidationResult.ACCEPT, ValidationResult.ACCEPT)))
        assertEquals(ValidationResult.NOP, service.reduce(listOf(ValidationResult.NOP, ValidationResult.ACCEPT)))
    }

    @Test
    fun `downgrade accepting by default`() {
        assertTrue(TestValidator(ValidationResult.REJECT).isDowngradeAccepting)
    }

    @Test
    fun `downgrade price`() {
        checkDownGrade(2.0, ValidationResult.ACCEPT)
    }

    @Test
    fun `upgrade price`() {
        checkDownGrade(2.4, null)
    }

    private fun checkDownGrade(newPrice: Double, expected: ValidationResult?) {
        val oldBet = Bet(marketId = "1", selectionId = 1, requestedPrice = Price(2.2, 2.0, Side.LAY),
                placedDate = Instant.now(), matchedAmount = 5.0)
        val rejecting = TestValidator(ValidationResult.REJECT)
        val result = service.handleDowngrade(
                Price(newPrice, 2.0, Side.LAY),
                oldBet, rejecting.isDowngradeAccepting)
        assertEquals(expected, result)
        assertEquals(ValidationResult.REJECT, rejecting.validate(HOME_EVENT.copy()))
    }


    private data class TestValidator(private val result: ValidationResult) : Validator {

        override fun validate(event: BetEvent): ValidationResult {
            return result
        }
    }
}

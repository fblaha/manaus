package cz.fb.manaus.reactor.betting.validator

import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.homeContext
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidationServiceTest : AbstractLocalTestCase() {
    @Autowired
    private lateinit var service: ValidationService

    @Test
    fun `reduce ACCEPT - REJECT combinations`() {
        assertEquals(ValidationResult.REJECT, service.reduce(listOf(ValidationResult.ACCEPT, ValidationResult.REJECT, ValidationResult.ACCEPT)))
        assertEquals(ValidationResult.ACCEPT, service.reduce(listOf(ValidationResult.ACCEPT, ValidationResult.ACCEPT)))
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
                oldBet, rejecting)
        assertEquals(expected, result)
        assertEquals(ValidationResult.REJECT, rejecting.validate(homeContext.copy()))
    }


    private data class TestValidator(private val result: ValidationResult) : Validator {

        override fun validate(context: BetContext): ValidationResult {
            return result
        }
    }
}

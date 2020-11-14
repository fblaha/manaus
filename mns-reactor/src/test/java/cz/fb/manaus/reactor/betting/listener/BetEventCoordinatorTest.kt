package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.BetActionType
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractTestCase
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.HOME_EVENT_BACK
import cz.fb.manaus.reactor.betting.PriceAdviser
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.proposer.ProposedPrice
import cz.fb.manaus.reactor.betting.validator.ValidationCoordinator
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.ValidationResult.*
import cz.fb.manaus.reactor.betting.validator.ValidationService
import cz.fb.manaus.reactor.betting.validator.Validator
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNull


class MockValidator(
        private val result: ValidationResult
) : Validator {
    override fun validate(event: BetEvent): ValidationResult {
        return result
    }
}

class MockPriceProposer(
        private val validationResult: ValidationResult,
        private val price: Double?
) : PriceProposer {

    override fun getProposedPrice(event: BetEvent): Double? {
        return price
    }

    override fun validate(event: BetEvent): ValidationResult {
        return validationResult
    }

}

class MockPriceAdviser(
        private val price: Price?
) : PriceAdviser {

    override fun getNewPrice(betEvent: BetEvent): ProposedPrice<Price>? {
        return price?.let { ProposedPrice(it, setOf("test")) }
    }

}

class BetEventCoordinatorTest : AbstractTestCase() {

    @Autowired
    private lateinit var validationService: ValidationService

    @Test
    fun `happy path`() {
        val price = Price(3.0, 3.0, Side.BACK)
        val coordinator = createCoordinator(price, OK, OK)
        val (bet) = coordinator.onBetEvent(HOME_EVENT_BACK)!!
        val action = bet.action ?: error("no action")
        assertEquals(price, bet.requestedPrice)
        assertEquals(price, action.price)
        assertEquals(BetActionType.PLACE, action.betActionType)
    }

    @Test
    fun `validation failed`() {
        val price = Price(3.0, 3.0, Side.BACK)
        assertNull(createCoordinator(price, DROP, OK).onBetEvent(HOME_EVENT_BACK))
        assertNull(createCoordinator(price, NOP, OK).onBetEvent(HOME_EVENT_BACK))
        assertNull(createCoordinator(price, OK, DROP).onBetEvent(HOME_EVENT_BACK))
        assertNull(createCoordinator(price, OK, NOP).onBetEvent(HOME_EVENT_BACK))
    }

    @Test
    fun `propose failed`() {
        assertNull(createCoordinator(null, OK, OK).onBetEvent(HOME_EVENT_BACK))
    }

    private fun createCoordinator(
            price: Price?,
            prePriceValidation: ValidationResult,
            priceValidation: ValidationResult
    ): BetEventCoordinator {
        val proposerAdviser = MockPriceProposer(prePriceValidation, price?.price)
        return BetEventCoordinator(
                side = Side.BACK,
                validationCoordinator = ValidationCoordinator(
                        validators = listOf(MockValidator(priceValidation), proposerAdviser),
                        validationService = validationService
                ),
                priceAdviser = MockPriceAdviser(price)
        )
    }

}
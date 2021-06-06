package cz.fb.manaus.reactor.betting.proposer.common

import cz.fb.manaus.reactor.PricesTestFactory
import cz.fb.manaus.reactor.betting.HOME_EVENT_BACK
import cz.fb.manaus.reactor.betting.HOME_EVENT_LAY
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class BestPriceProposerTest {

    private val proposer = BestPriceProposer(0.01)

    @Test
    fun `lay propose`() {
        val event = HOME_EVENT_LAY.copy(marketPrices = PricesTestFactory.newMarketPrices(2.0, 4.5))
        assertEquals(ValidationResult.OK, proposer.validate(event))
        assertEquals(2.01, proposer.getProposedPrice(event))
    }

    @Test
    fun check() {
        assertEquals(ValidationResult.OK, proposer.validate(HOME_EVENT_LAY))
        assertEquals(ValidationResult.OK, proposer.validate(HOME_EVENT_BACK))
    }

    @Test
    fun `back propose`() {
        val prices = PricesTestFactory.newMarketPrices(2.5, 3.5)
        val context = HOME_EVENT_BACK.copy(marketPrices = prices)
        assertEquals(ValidationResult.OK, proposer.validate(context))
        assertEquals(3.475, proposer.getProposedPrice(context))
    }

}

package cz.fb.manaus.reactor.betting.proposer

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractTestCase5
import cz.fb.manaus.reactor.betting.FixedAmountAdviser
import cz.fb.manaus.reactor.betting.HOME_EVENT_BACK
import cz.fb.manaus.reactor.betting.HOME_EVENT_LAY
import cz.fb.manaus.reactor.betting.listener.MockPriceProposer
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals


class MinReduceProposerAdviserTest : AbstractTestCase5() {

    private lateinit var adviser: MinReduceProposerAdviser

    @Autowired
    private lateinit var proposalService: PriceProposalService

    @BeforeEach
    fun setUp() {
        val proposer1 = MockPriceProposer(ValidationResult.OK, 3.0)
        val proposer2 = MockPriceProposer(ValidationResult.OK, 3.5)
        val amountAdviser = FixedAmountAdviser(3.0)
        val proposers = listOf(proposer1, proposer2)
        adviser = MinReduceProposerAdviser(proposers, amountAdviser, proposalService)
    }

    @Test
    fun `back price`() {
        val backPrice = adviser.getNewPrice(HOME_EVENT_BACK).price
        assertEquals(Price(3.5, 3.0, Side.BACK), backPrice)
    }

    @Test
    fun `lay price`() {
        val backPrice = adviser.getNewPrice(HOME_EVENT_LAY).price
        assertEquals(Price(3.0, 3.0, Side.LAY), backPrice)
    }

    @Test
    fun proposers() {
        val proposers = adviser.getNewPrice(HOME_EVENT_BACK).proposers
        assertEquals(setOf("mockPriceProposer"), proposers)
    }

}
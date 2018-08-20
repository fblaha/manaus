package cz.fb.manaus.reactor.betting.proposer.common

import com.nhaarman.mockito_kotlin.mock
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import kotlin.test.assertEquals

class TheAbstractDecrementingProposerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var proposer: TestProposer

    @Test
    fun `propose`() {
        proposer.origPrice = 2.0
        assertEquals(1.98, proposer.getProposedPrice(mock()).asDouble)
    }

    @Test(expected = NoSuchElementException::class)
    fun `low boundary reached`() {
        proposer.origPrice = 1.02
        proposer.getProposedPrice(mock()).asDouble
    }

    @Test
    fun `reject - unable to propose`() {
        proposer.origPrice = 1.02
        assertEquals(ValidationResult.REJECT, proposer.validate(mock()))
    }

    @Test
    fun `able to propose price`() {
        proposer.origPrice = 1.2
        assertEquals(ValidationResult.ACCEPT, proposer.validate(mock()))
    }

    @Component
    private class TestProposer : AbstractDecrementingProposer(2) {
        internal var origPrice: Double = 0.0

        override fun getOriginalPrice(betContext: BetContext): Double {
            return origPrice
        }
    }

}
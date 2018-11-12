package cz.fb.manaus.reactor.charge

import com.google.common.collect.LinkedListMultimap
import com.google.common.collect.ListMultimap
import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.provider.ExchangeProvider
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Assert.assertTrue
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.function.BiConsumer


class MarketChargeSimulatorTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var simulator: MarketChargeSimulator
    @Autowired
    private lateinit var provider: ExchangeProvider


    private fun assertDown(before: Double, after: Double) {
        assertTrue(after < before)
    }

    private fun assertUp(before: Double, after: Double) {
        assertTrue(after > before)
    }

    @Test
    fun `counter matching`() {
        val bets = LinkedListMultimap.create<Long, Price>()
        checkSimulatedCharge(SEL_HOME, Price(1.8, 2.0, Side.LAY), bets, THREE_IMBALANCED, BiConsumer { before, after -> this.assertUp(before, after) })
        checkSimulatedCharge(SEL_HOME, Price(2.2, 2.0, Side.BACK), bets, THREE_IMBALANCED, BiConsumer { before, after -> this.assertDown(before, after) })
        checkSimulatedCharge(SEL_AWAY, Price(9.0, 2.0, Side.BACK), bets, THREE_IMBALANCED, BiConsumer { before, after -> this.assertUp(before, after) })
        checkSimulatedCharge(SEL_AWAY, Price(6.0, 2.0, Side.LAY), bets, THREE_IMBALANCED, BiConsumer { before, after -> this.assertDown(before, after) })
    }

    @Test
    fun `back matching 3`() {
        val bets = LinkedListMultimap.create<Long, Price>()
        checkSimulatedCharge(SEL_HOME, Price(3.5, 2.0, Side.BACK), bets, THREE_BALANCED, BiConsumer { before, after -> this.assertUp(before, after) })
        checkSimulatedCharge(SEL_DRAW, Price(3.5, 2.0, Side.BACK), bets, THREE_BALANCED, BiConsumer { before, after -> this.assertUp(before, after) })
        checkSimulatedCharge(SEL_AWAY, Price(3.5, 2.0, Side.BACK), bets, THREE_BALANCED, BiConsumer { before, after -> this.assertDown(before, after) })
    }

    @Test
    fun `low matched amount`() {
        val bets = LinkedListMultimap.create<Long, Price>()
        checkSimulatedCharge(SEL_HOME, Price(2.1, 0.75, Side.BACK), bets, TWO_BALANCED, BiConsumer { before, after -> this.assertUp(before, after) })
        checkSimulatedCharge(SEL_HOME, Price(1.9, 2.0, Side.LAY), bets, TWO_BALANCED, BiConsumer { before, after -> this.assertUp(before, after) })

        checkSimulatedCharge(SEL_HOME, Price(2.5, 1.0, Side.BACK), bets, TWO_BALANCED, BiConsumer { before, after -> this.assertDown(before, after) })
        checkSimulatedCharge(SEL_HOME, Price(2.5, 1.0, Side.BACK), bets, TWO_BALANCED, BiConsumer { before, after -> this.assertUp(before, after) })
    }

    @Test
    fun `back matching 2`() {
        val bets = LinkedListMultimap.create<Long, Price>()
        checkSimulatedCharge(SEL_HOME, Price(2.1, 2.0, Side.BACK), bets, TWO_BALANCED, BiConsumer { before, after -> this.assertUp(before, after) })
        checkSimulatedCharge(SEL_AWAY, Price(2.1, 2.0, Side.BACK), bets, TWO_BALANCED, BiConsumer { before, after -> this.assertDown(before, after) })
    }

    @Test
    fun `lay matching 2`() {
        val bets = LinkedListMultimap.create<Long, Price>()
        checkSimulatedCharge(SEL_HOME, Price(1.8, 2.0, Side.LAY), bets, TWO_BALANCED, BiConsumer { before, after -> this.assertUp(before, after) })
        checkSimulatedCharge(SEL_AWAY, Price(1.9, 2.0, Side.LAY), bets, TWO_BALANCED, BiConsumer { before, after -> this.assertDown(before, after) })
    }

    @Test
    fun `all green cross matching`() {
        val bets = LinkedListMultimap.create<Long, Price>()
        checkSimulatedCharge(SEL_HOME, Price(1.8, 2.0, Side.LAY), bets, TWO_BALANCED, BiConsumer { before, after -> this.assertUp(before, after) })
        checkSimulatedCharge(SEL_AWAY, Price(2.2, 2.0, Side.BACK), bets, TWO_BALANCED, BiConsumer { before, after -> this.assertUp(before, after) })
        checkSimulatedCharge(SEL_AWAY, Price(1.9, 2.0, Side.LAY), bets, TWO_BALANCED, BiConsumer { before, after -> this.assertDown(before, after) })
        checkSimulatedCharge(SEL_HOME, Price(2.3, 2.0, Side.BACK), bets, TWO_BALANCED, BiConsumer { before, after -> this.assertDown(before, after) })
    }

    @Test
    fun `lay matching 3`() {
        val bets = LinkedListMultimap.create<Long, Price>()
        checkSimulatedCharge(SEL_HOME, Price(2.7, 2.5, Side.LAY), bets, THREE_BALANCED, BiConsumer { before, after -> this.assertUp(before, after) })
        checkSimulatedCharge(SEL_DRAW, Price(3.0, 2.0, Side.LAY), bets, THREE_BALANCED, BiConsumer { before, after -> this.assertDown(before, after) })
        checkSimulatedCharge(SEL_AWAY, Price(2.9, 2.0, Side.LAY), bets, THREE_BALANCED, BiConsumer { before, after -> this.assertDown(before, after) })
    }

    private fun checkSimulatedCharge(selection: Long, newBet: Price, bets: ListMultimap<Long, Price>,
                                     probabilities: Map<Long, Double>, assertion: BiConsumer<Double, Double>) {
        val before = simulator.getChargeMean(1, provider.chargeRate, probabilities, bets)
        bets.put(selection, newBet)
        val after = simulator.getChargeMean(1, provider.chargeRate, probabilities, bets)
        assertion.accept(before, after)
    }

    companion object {

        val THREE_IMBALANCED = mapOf(
                SEL_HOME to 0.5,
                SEL_DRAW to 0.3,
                SEL_AWAY to 0.2)

        val THREE_BALANCED = mapOf(
                SEL_HOME to 0.34,
                SEL_DRAW to 0.33,
                SEL_AWAY to 0.33)

        val TWO_BALANCED = mapOf(
                SEL_HOME to 0.5,
                SEL_AWAY to 0.5)
    }
}
package cz.fb.manaus.reactor.charge

import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Assert.assertTrue
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired


class MarketChargeSimulatorTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var simulator: MarketChargeSimulator


    private val threeImbalanced = mapOf(
        SEL_HOME to 0.5,
        SEL_DRAW to 0.3,
        SEL_AWAY to 0.2
    )

    private val threeBalanced = mapOf(
        SEL_HOME to 0.34,
        SEL_DRAW to 0.33,
        SEL_AWAY to 0.33
    )

    private val twoBalanced = mapOf(
        SEL_HOME to 0.5,
        SEL_AWAY to 0.5
    )

    private fun assertDown(before: Double, after: Double) {
        assertTrue(after < before)
    }

    private fun assertUp(before: Double, after: Double) {
        assertTrue(after > before)
    }

    @Test
    fun `counter matching`() {
        val bets = mutableMapOf<Long, MutableList<Price>>()
        checkSimulatedCharge(
            SEL_HOME,
            Price(1.8, 2.0, Side.LAY),
            bets,
            threeImbalanced
        ) { before, after -> this.assertUp(before, after) }
        checkSimulatedCharge(
            SEL_HOME,
            Price(2.2, 2.0, Side.BACK),
            bets,
            threeImbalanced
        ) { before, after -> this.assertDown(before, after) }
        checkSimulatedCharge(
            SEL_AWAY,
            Price(9.0, 2.0, Side.BACK),
            bets,
            threeImbalanced
        ) { before, after -> this.assertUp(before, after) }
        checkSimulatedCharge(
            SEL_AWAY,
            Price(6.0, 2.0, Side.LAY),
            bets,
            threeImbalanced
        ) { before, after -> this.assertDown(before, after) }
    }

    @Test
    fun `back matching 3`() {
        val bets = mutableMapOf<Long, MutableList<Price>>()
        checkSimulatedCharge(
            SEL_HOME,
            Price(3.5, 2.0, Side.BACK),
            bets,
            threeBalanced
        ) { before, after -> this.assertUp(before, after) }
        checkSimulatedCharge(
            SEL_DRAW,
            Price(3.5, 2.0, Side.BACK),
            bets,
            threeBalanced
        ) { before, after -> this.assertUp(before, after) }
        checkSimulatedCharge(
            SEL_AWAY,
            Price(3.5, 2.0, Side.BACK),
            bets,
            threeBalanced
        ) { before, after -> this.assertDown(before, after) }
    }

    @Test
    fun `low matched amount`() {
        val bets = mutableMapOf<Long, MutableList<Price>>()
        checkSimulatedCharge(SEL_HOME, Price(2.1, 0.75, Side.BACK), bets, twoBalanced) { before, after ->
            this.assertUp(
                before,
                after
            )
        }
        checkSimulatedCharge(SEL_HOME, Price(1.9, 2.0, Side.LAY), bets, twoBalanced) { before, after ->
            this.assertUp(
                before,
                after
            )
        }

        checkSimulatedCharge(
            SEL_HOME,
            Price(2.5, 1.0, Side.BACK),
            bets,
            twoBalanced
        ) { before, after -> this.assertDown(before, after) }
        checkSimulatedCharge(SEL_HOME, Price(2.5, 1.0, Side.BACK), bets, twoBalanced) { before, after ->
            this.assertUp(
                before,
                after
            )
        }
    }

    @Test
    fun `back matching 2`() {
        val bets = mutableMapOf<Long, MutableList<Price>>()
        checkSimulatedCharge(SEL_HOME, Price(2.1, 2.0, Side.BACK), bets, twoBalanced) { before, after ->
            this.assertUp(
                before,
                after
            )
        }
        checkSimulatedCharge(
            SEL_AWAY,
            Price(2.1, 2.0, Side.BACK),
            bets,
            twoBalanced
        ) { before, after -> this.assertDown(before, after) }
    }

    @Test
    fun `lay matching 2`() {
        val bets = mutableMapOf<Long, MutableList<Price>>()
        checkSimulatedCharge(SEL_HOME, Price(1.8, 2.0, Side.LAY), bets, twoBalanced) { before, after ->
            this.assertUp(
                before,
                after
            )
        }
        checkSimulatedCharge(SEL_AWAY, Price(1.9, 2.0, Side.LAY), bets, twoBalanced) { before, after ->
            this.assertDown(
                before,
                after
            )
        }
    }

    @Test
    fun `all green cross matching`() {
        val bets = mutableMapOf<Long, MutableList<Price>>()
        checkSimulatedCharge(SEL_HOME, Price(1.8, 2.0, Side.LAY), bets, twoBalanced) { before, after ->
            this.assertUp(
                before,
                after
            )
        }
        checkSimulatedCharge(SEL_AWAY, Price(2.2, 2.0, Side.BACK), bets, twoBalanced) { before, after ->
            this.assertUp(
                before,
                after
            )
        }
        checkSimulatedCharge(SEL_AWAY, Price(1.9, 2.0, Side.LAY), bets, twoBalanced) { before, after ->
            this.assertDown(
                before,
                after
            )
        }
        checkSimulatedCharge(
            SEL_HOME,
            Price(2.3, 2.0, Side.BACK),
            bets,
            twoBalanced
        ) { before, after -> this.assertDown(before, after) }
    }

    @Test
    fun `lay matching 3`() {
        val bets = mutableMapOf<Long, MutableList<Price>>()
        checkSimulatedCharge(SEL_HOME, Price(2.7, 2.5, Side.LAY), bets, threeBalanced) { before, after ->
            this.assertUp(
                before,
                after
            )
        }
        checkSimulatedCharge(
            SEL_DRAW,
            Price(3.0, 2.0, Side.LAY),
            bets,
            threeBalanced
        ) { before, after -> this.assertDown(before, after) }
        checkSimulatedCharge(
            SEL_AWAY,
            Price(2.9, 2.0, Side.LAY),
            bets,
            threeBalanced
        ) { before, after -> this.assertDown(before, after) }
    }

    private fun checkSimulatedCharge(
        selection: Long,
        newBet: Price,
        bets: MutableMap<Long, MutableList<Price>>,
        probabilities: Map<Long, Double>,
        assertion: (Double, Double) -> Unit
    ) {
        val before = simulator.getChargeMean(1, bfProvider.commission, probabilities, bets)
        bets.getOrPut(selection) { mutableListOf() }.add(newBet)
        val after = simulator.getChargeMean(1, bfProvider.commission, probabilities, bets)
        assertion(before, after)
    }
}
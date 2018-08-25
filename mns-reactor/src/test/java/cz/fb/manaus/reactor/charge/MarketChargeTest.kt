package cz.fb.manaus.reactor.charge

import com.google.common.collect.ImmutableMap.of
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.provider.ExchangeProvider
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.core.test.ModelFactory
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.test.assertEquals

class MarketChargeTest : AbstractLocalTestCase() {
    @Autowired
    private lateinit var provider: ExchangeProvider
    private val current = Date()
    /**
     * Comm Charged 5% On Net winnings of EUR0.2	 	 -	-	-	(0.01)	 	1,298.08
     * 16:36	 Qualifying Matches Bozoljac v Hernych / Match Odds / Ilia Bozoljac     Back	 2.98	2.00	Lost	(2.00)	 	1,298.09
     * 16:13	 Qualifying Matches Bozoljac v Hernych / Match Odds / Ilia Bozoljac     Lay	 	 2.60	2.00	Won	 	2.00    	1,300.09
     * 17:11	 Qualifying Matches Bozoljac v Hernych / Match Odds / Jan Hernych       Back 	 1.63	2.00	Won	 	1.26	    1,298.09
     * 16:36	 Qualifying Matches Bozoljac v Hernych / Match Odds / Jan Hernych       Lay	 	 1.53	2.00	Lost	(1.06)	 	1,296.83
     */

    private val sel1: Long = 11
    private val sel2: Long = 22

    private val back1 = mockAction(
            ModelFactory.newSettled(sel1, "Ilia Bozoljac", -2.0, current, Price(2.98, 2.0, Side.BACK)), "1")
    private val lay1 = mockAction(
            ModelFactory.newSettled(sel1, "Ilia Bozoljac", 2.0, current, Price(2.6, 2.0, Side.LAY)), "2")
    private val back2 = mockAction(
            ModelFactory.newSettled(sel2, "Jan Hernych", 1.26, current, Price(1.63, 2.0, Side.BACK)), "3")
    private val lay2 = mockAction(
            ModelFactory.newSettled(sel2, "Jan Hernych", -1.06, current, Price(1.53, 2.0, Side.LAY)), "4")


    @Test
    fun `low profit charge`() {
        val charge = MarketCharge.fromBets(provider.chargeRate, listOf(lay1, lay2, back1, back2))
        checkCharge(charge, 0.013, 0.2, of("2", 0.01, "3", 0.0))
    }

    @Test
    fun `high profit charge`() {
        val charge = MarketCharge.fromBets(provider.chargeRate, listOf(lay1, lay2, back2))
        checkCharge(charge, 0.143, 2.2, of("2", 0.09, "3", 0.05))
    }

    @Test
    fun `loss - expected 0 charge`() {
        val charge = MarketCharge.fromBets(provider.chargeRate, listOf(lay1, lay2, back1))
        checkCharge(charge, 0.0, -1.06, of("2", 0.0, "3", 0.0))
    }

    @Test
    fun `1 loss 1 profit`() {
        val charge = MarketCharge.fromBets(provider.chargeRate, listOf(lay1, lay2))
        checkCharge(charge, 0.061, 0.94, of("2", 0.06, "4", 0.0))
    }

    private fun checkCharge(charge: MarketCharge, totalCharge: Double, totalProfit: Double,
                            expectedContributions: Map<String, Double>) {
        assertEquals(totalCharge, charge.totalCharge)
        assertEquals(totalProfit, charge.totalProfit)
        for ((key, value) in expectedContributions) {
            Assert.assertEquals(value, charge.getChargeContribution(key), 0.01)
        }
    }

    private fun mockAction(bet: SettledBet, betId: String): SettledBet {
        val mock = mock<BetAction>()
        whenever(mock.betId).thenReturn(betId)
        bet.betAction = mock
        return bet
    }
}

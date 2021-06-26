package cz.fb.manaus.core.batch

import cz.fb.manaus.core.model.betAction
import cz.fb.manaus.core.model.homeSettledBet
import cz.fb.manaus.core.model.market
import cz.fb.manaus.core.test.AbstractIntegrationTestCase
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class RealizedBetLoaderTest : AbstractIntegrationTestCase() {

    @Autowired
    private lateinit var realizedBetLoader: RealizedBetLoader

    @Test
    fun toRealizedBet() {
        marketRepository.save(market)
        val savedAction = betAction.copy(betId = "1000")
        val actionId = betActionRepository.save(savedAction).id
        val savedBet = homeSettledBet.copy(id = "1000")
        val (bet, action, market) = realizedBetLoader.toRealizedBet(savedBet)
        assertEquals(savedBet, bet)
        assertEquals(market, market)
        assertEquals(savedAction.copy(id = actionId), action)
    }
}
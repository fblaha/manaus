package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.betAction
import cz.fb.manaus.core.model.marketTemplate
import cz.fb.manaus.core.model.settledBet
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import org.dizitart.no2.objects.filters.ObjectFilters
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class RealizedBetLoaderTest : AbstractDatabaseTestCase() {
    @Autowired
    private lateinit var marketRepository: MarketRepository
    @Autowired
    private lateinit var betActionRepository: BetActionRepository
    @Autowired
    private lateinit var realizedBetLoader: RealizedBetLoader

    @Before
    fun setUp() {
        marketRepository.repository.remove(ObjectFilters.ALL)
        betActionRepository.repository.remove(ObjectFilters.ALL)
    }

    @Test
    fun toRealizedBet() {
        marketRepository.saveOrUpdate(marketTemplate)
        val savedAction = betAction.copy(betID = "1000")
        val actionID = betActionRepository.save(savedAction)
        val savedBet = settledBet.copy(id = "1000")
        val (bet, action, market) = realizedBetLoader.toRealizedBet(savedBet)
        assertEquals(savedBet, bet)
        assertEquals(marketTemplate, market)
        assertEquals(savedAction.copy(id = actionID), action)
    }
}
package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.MarketFootprint
import cz.fb.manaus.core.model.betAction
import cz.fb.manaus.core.model.homeSettledBet
import cz.fb.manaus.core.model.market
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MarketPurgerTest : AbstractDatabaseTestCase() {

    @Autowired
    private lateinit var marketPurger: MarketPurger

    @Test
    fun `purge inactive`() {
        marketRepository.saveOrUpdate(market)
        assertNotNull(marketRepository.read(market.id))
        marketPurger.purge(MarketFootprint(market, emptyList(), emptyList()))
        assertNull(marketRepository.read(market.id))
    }

    @Test
    fun `purge active`() {
        marketRepository.saveOrUpdate(market)
        betActionRepository.save(betAction)
        settledBetRepository.save(homeSettledBet)
        assertNotNull(marketRepository.read(market.id))
        assertNotNull(settledBetRepository.read(homeSettledBet.id))
        assertTrue(betActionRepository.find(market.id).isNotEmpty())

        marketPurger.purge(MarketFootprint(market, listOf(betAction), listOf(homeSettledBet)))
        assertNull(marketRepository.read(market.id))
        assertTrue(betActionRepository.find(market.id).isEmpty())
        assertNull(settledBetRepository.read(homeSettledBet.id))
    }
}
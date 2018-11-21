package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.betAction
import cz.fb.manaus.core.model.homeSettledBet
import cz.fb.manaus.core.model.market
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertTrue

class MarketFootprintLoaderTest : AbstractDatabaseTestCase() {

    @Autowired
    private lateinit var marketFootprintLoader: MarketFootprintLoader

    @Test
    fun load() {
        marketRepository.saveOrUpdate(market)

        var footprint = marketFootprintLoader.toFootprint(market)
        assertTrue(footprint.betActions.isEmpty())
        assertTrue(footprint.settledBets.isEmpty())

        betActionRepository.save(betAction)
        settledBetRepository.save(homeSettledBet)
        footprint = marketFootprintLoader.toFootprint(market)
        assertTrue(footprint.betActions.isNotEmpty())
        assertTrue(footprint.settledBets.isNotEmpty())
    }
}
package cz.fb.manaus.core.maintanance.db

import cz.fb.manaus.core.model.betAction
import cz.fb.manaus.core.model.market
import cz.fb.manaus.core.test.AbstractIntegrationTestCase
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.ExperimentalTime

@ExperimentalTime
class MarketCleanerTest : AbstractIntegrationTestCase() {

    @Autowired
    private lateinit var marketCleaner: MarketCleaner

    @Test
    fun `execute - empty market`() {
        saveMarket(4)
        marketCleaner.execute()
        assertNull(marketRepository.read(market.id))
    }

    @Test
    fun `execute - not empty market`() {
        saveMarket(4)
        betActionRepository.save(betAction)
        marketCleaner.execute()
        assertNotNull(marketRepository.read(market.id))
    }

    @Test
    fun `execute - old market`() {
        saveMarket(1000)
        marketCleaner.execute()
        assertNull(marketRepository.read(market.id))
    }

    @Test
    fun `execute - too early`() {
        saveMarket(30)
        marketCleaner.execute()
        assertNotNull(marketRepository.read(market.id))
    }

    private fun saveMarket(daysBack: Long) {
        val event = market.event.copy(openDate = Instant.now().minus(daysBack, ChronoUnit.DAYS))
        marketRepository.save(market.copy(event = event))
        assertNotNull(marketRepository.read(market.id))
    }
}
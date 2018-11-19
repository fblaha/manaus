package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.market
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals

class MarketPurgerTest : AbstractDatabaseTestCase() {

    @Autowired
    private lateinit var marketPurger: MarketPurger

    @Test
    fun `purge inactive`() {
        marketRepository.saveOrUpdate(market)
        assertEquals(0, marketPurger.purgeInactive(Instant.now()))
        assertEquals(1, marketPurger.purgeInactive(Instant.now().plus(3, ChronoUnit.DAYS)))
    }
}
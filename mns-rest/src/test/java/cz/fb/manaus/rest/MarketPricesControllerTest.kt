package cz.fb.manaus.rest

import cz.fb.manaus.core.dao.AbstractDaoTest
import cz.fb.manaus.core.test.CoreTestFactory
import org.junit.Test
import org.springframework.test.context.ContextConfiguration


@ContextConfiguration(classes = [MarketPricesController::class])
class MarketPricesControllerTest : AbstractControllerTest() {

    @Test
    fun prices() {
        createMarketWithSingleAction()
        checkResponse("/markets/" + AbstractDaoTest.MARKET_ID + "/prices", "lastMatchedPrice", "selectionId", "prices")
        checkResponse("/markets/" + AbstractDaoTest.MARKET_ID + "/prices/" + CoreTestFactory.DRAW,
                "lastMatchedPrice", "selectionId", "prices")
    }
}
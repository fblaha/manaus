package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.BetActionType
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.betTemplate
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.betting.HOME_EVENT
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class BetCommandIssuerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var betCommandIssuer: BetCommandIssuer

    @Test
    fun placeOrUpdate() {
        val homeEvent = HOME_EVENT
        val price = Price(3.0, 3.0, Side.BACK)
        homeEvent.newPrice = price
        val command = betCommandIssuer.placeOrUpdate(homeEvent)
        assertEquals(price, command.action?.price)
        assertEquals(BetActionType.PLACE, command.action?.betActionType)
        assertEquals(price, command.bet.requestedPrice)
    }

    @Test
    fun tryCancel() {
        assertNull(betCommandIssuer.tryCancel(null))
        assertNotNull(betCommandIssuer.tryCancel(betTemplate))
    }
}
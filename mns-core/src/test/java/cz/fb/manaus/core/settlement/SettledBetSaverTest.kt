package cz.fb.manaus.core.settlement

import cz.fb.manaus.core.dao.AbstractDaoTest
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.SettledBetTest
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.CoreTestFactory
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.test.assertEquals

class SettledBetSaverTest : AbstractDaoTest() {

    @Autowired
    private lateinit var saver: SettledBetSaver

    @Test
    fun testSaver() {
        createMarketWithSingleAction()
        assertEquals(SaveStatus.OK, saver.saveBet(AbstractDaoTest.BET_ID, createAction()))
        assertEquals(SaveStatus.COLLISION, saver.saveBet(AbstractDaoTest.BET_ID, createAction()))
        assertEquals(SaveStatus.NO_ACTION, saver.saveBet(AbstractDaoTest.BET_ID + "x", createAction()))
    }

    private fun createAction(): SettledBet {
        return SettledBetTest.create(CoreTestFactory.DRAW, CoreTestFactory.DRAW_NAME, 5.0, Date(), Price(3.0, 3.0, Side.LAY))
    }

}
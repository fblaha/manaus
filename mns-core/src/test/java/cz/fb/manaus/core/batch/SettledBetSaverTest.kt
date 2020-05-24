package cz.fb.manaus.core.batch

import cz.fb.manaus.core.model.betAction
import cz.fb.manaus.core.model.homeSettledBet
import cz.fb.manaus.core.model.market
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettledBetSaverTest : AbstractDatabaseTestCase() {

    @Autowired
    private lateinit var saver: SettledBetSaver

    @Test
    fun saver() {
        marketRepository.saveOrUpdate(market)
        betActionRepository.idSafeSave(betAction.copy(betId = "testSaver"))

        val bet = homeSettledBet.copy(id = "testSaver")
        assertTrue(saver.saveBet(bet))
        assertFalse(saver.saveBet(bet))

        assertTrue(saver.saveBet(bet.copy(commission = 0.01)))
        assertFalse(saver.saveBet(bet.copy(id = "missing")))
    }
}
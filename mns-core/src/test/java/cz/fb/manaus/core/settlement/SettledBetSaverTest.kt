package cz.fb.manaus.core.settlement

import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.core.repository.MarketRepository
import cz.fb.manaus.core.repository.domain.betAction
import cz.fb.manaus.core.repository.domain.marketTemplate
import cz.fb.manaus.core.repository.domain.settledBet
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class SettledBetSaverTest : AbstractDatabaseTestCase() {

    @Autowired
    private lateinit var saver: SettledBetSaver
    @Autowired
    private lateinit var marketRepository: MarketRepository
    @Autowired
    private lateinit var betActionRepository: BetActionRepository

    @Test
    fun testSaver() {
        marketRepository.saveOrUpdate(marketTemplate)
        betActionRepository.save(betAction.copy(betID = "testSaver"))

        val bet = settledBet.copy(id = "testSaver")
        assertEquals(SaveStatus.OK, saver.saveBet(bet))
        assertEquals(SaveStatus.COLLISION, saver.saveBet(bet))
        assertEquals(SaveStatus.NO_ACTION, saver.saveBet(bet.copy(id = "missing")))
    }
}
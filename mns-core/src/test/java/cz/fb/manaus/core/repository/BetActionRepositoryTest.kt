package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.betAction
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue


class BetActionRepositoryTest : AbstractDatabaseTestCase() {

    @Autowired
    private lateinit var betActionRepository: BetActionRepository
    @Autowired
    private lateinit var cleaner: DatabaseCleaner

    @Before
    fun setUp() {
        cleaner.clean()
    }

    @Test
    fun save() {
        assertTrue(betActionRepository.save(betAction) != 0L)
    }

    @Test
    fun `set bet ID`() {
        val actionID = betActionRepository.save(betAction.copy(betID = null))
        assertNull(betActionRepository.find("2").first().betID)
        betActionRepository.setBetID(actionID, "100")
        assertEquals(1, betActionRepository.find("2").size)
        assertEquals("100", betActionRepository.find("2").first().betID)
    }

    @Test
    fun find() {
        betActionRepository.save(betAction)
        assertEquals(1, betActionRepository.find("2").size)
        assertEquals(0, betActionRepository.find("3").size)
    }

    @Test
    fun delete() {
        betActionRepository.save(betAction)
        assertEquals(1, betActionRepository.find("2").size)
        assertEquals(1, betActionRepository.delete("2"))
        assertEquals(0, betActionRepository.find("2").size)
    }

    @Test
    fun `get recent action`() {
        val actionTime = betAction.time
        val recent = betActionRepository.save(betAction)
        betActionRepository.setBetID(recent, "100")
        val older = betActionRepository.save(betAction.copy(time = actionTime.minusSeconds(600)))
        betActionRepository.setBetID(older, "100")
        assertEquals(recent, betActionRepository.findRecentBetAction("100")!!.id)
    }

    @Test
    fun `get recent actions`() {
        val actionTime = betAction.time
        val recent = betActionRepository.save(betAction)
        val older = betActionRepository.save(betAction.copy(time = actionTime.minusSeconds(600)))
        val actions = betActionRepository.findRecentBetActions(100)
        assertEquals(2, actions.size)
        assertEquals(recent, actions.first().id)
        assertEquals(older, actions.last().id)
        assertEquals(1, betActionRepository.findRecentBetActions(1).size)
    }
}
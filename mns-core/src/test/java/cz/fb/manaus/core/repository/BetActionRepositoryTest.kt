package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.betAction
import cz.fb.manaus.core.test.AbstractIntegrationTestCase
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue


class BetActionRepositoryTest : AbstractIntegrationTestCase() {

    @Test
    fun save() {
        assertTrue(betActionRepository.idSafeSave(betAction) != 0L)
    }

    @Test
    fun `set bet ID`() {
        val actionId = betActionRepository.idSafeSave(betAction.copy(betId = null))
        assertNull(betActionRepository.find("2").first().betId)
        betActionRepository.setBetId(actionId, "100")
        assertEquals(1, betActionRepository.find("2").size)
        assertEquals("100", betActionRepository.find("2").first().betId)
    }

    @Test
    fun find() {
        betActionRepository.idSafeSave(betAction)
        assertEquals(1, betActionRepository.find("2").size)
        assertEquals(0, betActionRepository.find("3").size)
    }

    @Test
    fun delete() {
        betActionRepository.idSafeSave(betAction)
        assertEquals(1, betActionRepository.find("2").size)
        assertEquals(1, betActionRepository.deleteByMarket("2"))
        assertEquals(0, betActionRepository.find("2").size)
    }

    @Test
    fun `get recent action`() {
        val actionTime = betAction.time
        val recent = betActionRepository.idSafeSave(betAction)
        betActionRepository.setBetId(recent, "100")
        val older = betActionRepository.idSafeSave(betAction.copy(time = actionTime.minusSeconds(600)))
        betActionRepository.setBetId(older, "100")
        assertEquals(recent, betActionRepository.findRecentBetAction("100")!!.id)
    }

    @Test
    fun `get recent actions`() {
        val actionTime = betAction.time
        val recent = betActionRepository.idSafeSave(betAction)
        val older = betActionRepository.idSafeSave(betAction.copy(time = actionTime.minusSeconds(600)))
        val actions = betActionRepository.findRecentBetActions(100)
        assertEquals(2, actions.size)
        assertEquals(recent, actions.first().id)
        assertEquals(older, actions.last().id)
        assertEquals(1, betActionRepository.findRecentBetActions(1).size)
    }
}
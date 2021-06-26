package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.betAction
import cz.fb.manaus.core.test.AbstractIntegrationTestCase
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue


class BetActionRepositoryTest : AbstractIntegrationTestCase() {

    @Test
    fun save() {
        assertEquals(betAction, betActionRepository.save(betAction))
    }

    @Test
    fun `set bet ID`() {
        val actionId = betActionRepository.save(betAction.copy(betId = null)).id
        assertNull(betActionRepository.find("2").first().betId)
        betActionRepository.setBetId(actionId, "100")
        assertEquals(1, betActionRepository.find("2").size)
        assertEquals("100", betActionRepository.find("2").first().betId)
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
        assertEquals(1, betActionRepository.deleteByMarket("2"))
        assertEquals(0, betActionRepository.find("2").size)
    }

    @Test
    fun `get recent action`() {
        val actionTime = betAction.time
        val recent = betActionRepository.save(betAction).id
        betActionRepository.setBetId(recent, "100")
        val oldAction = betAction.copy(time = actionTime.minusSeconds(600))
        val older = betActionRepository.save(oldAction).id
        betActionRepository.setBetId(older, "100")
        assertEquals(recent, betActionRepository.findRecentBetAction("100")!!.id)
    }

    @Test
    fun `get recent actions`() {
        val actionTime = betAction.time
        val recent = betActionRepository.save(betAction).id
        val older = betActionRepository.save(
                betAction.copy(
                        time = actionTime.minusSeconds(600),
                        id = ObjectId().toString()
                )).id
        val actions = betActionRepository.findRecentBetActions(100)
        assertEquals(2, actions.size)
        assertEquals(recent, actions.first().id)
        assertEquals(older, actions.last().id)
        assertEquals(1, betActionRepository.findRecentBetActions(1).size)
    }

    @Test
    fun `save (new ID) - update`() {
        val noId = betAction.copy(id = "")
        val newId = betActionRepository.save(noId).id
        assertTrue { newId.isNotBlank() }
        val fromDB = betActionRepository.read(newId) ?: error("must")
        assertEquals(noId.copy(id = newId), fromDB)

        val updated = fromDB.copy(betId = "777")
        assertEquals(newId, betActionRepository.save(updated).id)
        assertEquals(updated, betActionRepository.read(newId) ?: error("must"))
        assertEquals(1, betActionRepository.list().size)
    }

}
package cz.fb.manaus.core.maintanance

import cz.fb.manaus.core.model.TaskExecution
import cz.fb.manaus.core.test.AbstractIntegrationTestCase
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PeriodicTaskManagerTest : AbstractIntegrationTestCase() {

    @Autowired
    private lateinit var periodicTaskManager: PeriodicTaskManager

    @Test
    fun cleanUp() {
        val name = "not-registered"
        taskExecutionRepository.save(TaskExecution(name, Instant.now()))
        assertNotNull(taskExecutionRepository.read(name))
        periodicTaskManager.cleanUp()
        assertNull(taskExecutionRepository.read(name))
    }

    @Test
    fun execute() {
        assertTrue(taskExecutionRepository.list().isEmpty())
        periodicTaskManager.executeExpired()
        assertTrue(taskExecutionRepository.list().isNotEmpty())
    }

    @Test
    fun `execute 2x`() {
        assertTrue(taskExecutionRepository.list().isEmpty())
        periodicTaskManager.executeExpired()
        val afterFirst = taskExecutionRepository.list()
        assertTrue(afterFirst.isNotEmpty())
        periodicTaskManager.executeExpired()
        assertEquals(afterFirst, taskExecutionRepository.list())
    }
}
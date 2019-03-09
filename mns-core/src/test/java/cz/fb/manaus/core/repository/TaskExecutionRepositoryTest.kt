package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.TaskExecution
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import org.junit.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TaskExecutionRepositoryTest : AbstractDatabaseTestCase() {

    @Test
    fun `save - read`() {
        val taskExecution = TaskExecution("test", Instant.now())
        taskExecutionRepository.saveOrUpdate(taskExecution)
        assertEquals(taskExecution, taskExecutionRepository.read(taskExecution.name))
    }

    @Test
    fun `read missing`() {
        assertNull(taskExecutionRepository.read("missing"))
    }

    @Test
    fun `save - update - read`() {
        val taskExecution = TaskExecution("test", Instant.now().minusMillis(1000))
        taskExecutionRepository.saveOrUpdate(taskExecution)
        val nextExecution = taskExecution.copy(time = Instant.now())
        taskExecutionRepository.saveOrUpdate(nextExecution)
        assertEquals(nextExecution, taskExecutionRepository.read(taskExecution.name))
    }

    @Test
    fun `save - delete`() {
        val taskExecution = TaskExecution("test", Instant.now())
        taskExecutionRepository.saveOrUpdate(taskExecution)
        assertNotNull(taskExecutionRepository.read(taskExecution.name))
        taskExecutionRepository.delete(taskExecution.name)
        assertNull(taskExecutionRepository.read(taskExecution.name))
    }

    @Test
    fun list() {
        val taskExecution = TaskExecution("test", Instant.now())
        assertEquals(0, taskExecutionRepository.list().size)
        taskExecutionRepository.saveOrUpdate(taskExecution)
        assertEquals(1, taskExecutionRepository.list().size)
        taskExecutionRepository.delete(taskExecution.name)
        assertEquals(0, taskExecutionRepository.list().size)
    }
}
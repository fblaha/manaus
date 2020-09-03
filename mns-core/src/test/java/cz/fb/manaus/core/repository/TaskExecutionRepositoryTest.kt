package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.TaskExecution
import cz.fb.manaus.core.test.AbstractIntegrationTestCase
import org.junit.Test
import java.time.Instant
import kotlin.test.assertEquals

class TaskExecutionRepositoryTest : AbstractIntegrationTestCase() {

    @Test
    fun `save - read`() {
        val taskExecution = TaskExecution("test", Instant.now())
        taskExecutionRepository.saveOrUpdate(taskExecution)
        assertEquals(taskExecution, taskExecutionRepository.read(taskExecution.name))
    }

}
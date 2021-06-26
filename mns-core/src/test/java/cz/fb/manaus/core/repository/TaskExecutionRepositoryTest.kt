package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.TaskExecution
import cz.fb.manaus.core.test.AbstractIntegrationTestCase
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals

class TaskExecutionRepositoryTest : AbstractIntegrationTestCase() {

    @Test
    fun `save - read`() {
        val taskExecution = TaskExecution("test1", Instant.now().truncatedTo(ChronoUnit.SECONDS))
        taskExecutionRepository.save(taskExecution)
        assertEquals(taskExecution, taskExecutionRepository.read(taskExecution.name))
    }

}
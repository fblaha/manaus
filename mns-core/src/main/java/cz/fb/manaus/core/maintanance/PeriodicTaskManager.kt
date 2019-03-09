package cz.fb.manaus.core.maintanance

import cz.fb.manaus.core.model.TaskExecution
import cz.fb.manaus.core.repository.TaskExecutionRepository
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.logging.Logger

@Component
class PeriodicTaskManager(
        private val periodicTasks: List<PeriodicTask>,
        private val taskExecutionRepository: TaskExecutionRepository
) {
    private val log = Logger.getLogger(PeriodicTaskManager::class.simpleName)

    fun cleanUp() {
        val registered = periodicTasks.map { it.name }.toSet()
        taskExecutionRepository.list()
                .filter { it.name !in registered }
                .onEach { log.info { "deleting orphan execution '$it'" } }
                .forEach { taskExecutionRepository.delete(it.name) }
    }

    fun executeExpired() {
        for (periodicTask in periodicTasks) {
            val execution = taskExecutionRepository.read(periodicTask.name)
            val now = Instant.now()
            if (execution == null || execution.time.plus(periodicTask.pausePeriod).isBefore(now)) {
                taskExecutionRepository.saveOrUpdate(TaskExecution(periodicTask.name, now))
                log.info { "executing task '${periodicTask.name}'" }
                periodicTask.execute()
            }
        }
    }
}
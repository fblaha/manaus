package cz.fb.manaus.core.maintanance

import cz.fb.manaus.core.model.TaskExecution
import cz.fb.manaus.core.repository.Repository
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.logging.Logger

@Component
@Profile(ManausProfiles.DB)
class PeriodicTaskManager(
        private val periodicTasks: List<PeriodicTask>,
        private val taskExecutionRepository: Repository<TaskExecution>
) {
    private val log = Logger.getLogger(PeriodicTaskManager::class.simpleName)

    fun cleanUp() {
        val registered = periodicTasks.map { it.name }.toSet()
        taskExecutionRepository.list()
                .filter { it.name !in registered }
                .onEach { log.info { "deleting orphan execution '$it'" } }
                .forEach { taskExecutionRepository.delete(it.name) }
    }

    @Scheduled(fixedDelayString = "PT5M")
    fun executeExpired() {
        cleanUp()
        for (periodicTask in periodicTasks) {
            log.info { "checking task '${periodicTask.name}'" }
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
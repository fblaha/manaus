package cz.fb.manaus.core.repository.mongo

import cz.fb.manaus.core.model.TaskExecution
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.stereotype.Component


@Component
@Profile(ManausProfiles.DB)
class MongoTaskExecutionRepository(
        operations: MongoOperations
) : MongoOperationsAware<TaskExecution> by MongoRepository(
        "name", TaskExecution::class.java, operations
)

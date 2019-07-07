package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.TaskExecution
import cz.fb.manaus.spring.ManausProfiles
import org.dizitart.kno2.getRepository
import org.dizitart.no2.Nitrite
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component


@Component
@Profile(ManausProfiles.DB)
class TaskExecutionRepository(private val db: Nitrite) :
        AbstractRepository<TaskExecution, String>({ db.getRepository {} }, TaskExecution::name)
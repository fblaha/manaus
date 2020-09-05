package cz.fb.manaus.core.repository.nitrite

import cz.fb.manaus.core.model.TaskExecution
import cz.fb.manaus.core.repository.Repository
import cz.fb.manaus.spring.ManausProfiles
import org.dizitart.kno2.getRepository
import org.dizitart.no2.Nitrite
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component


@Component
@Profile(ManausProfiles.DB)
class NO2TaskExecutionRepository(db: Nitrite) :
        Repository<TaskExecution> by NO2Repository(db.getRepository {}, TaskExecution::name)
package cz.fb.manaus.ischia.validator.update

import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.ischia.BackUniverse
import cz.fb.manaus.ischia.LayUniverse
import cz.fb.manaus.reactor.betting.validator.Validator
import cz.fb.manaus.reactor.betting.validator.common.update.DelayUpdateValidator
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

import java.time.Duration

@BackUniverse
@LayUniverse
@Component
@Profile(ManausProfiles.DB)
class DelayUpdateValidator(betActionRepository: BetActionRepository) :
    Validator by DelayUpdateValidator(Duration.ofMinutes(10), betActionRepository)

package cz.fb.manaus.ischia.validator

import cz.fb.manaus.ischia.BackUniverse
import cz.fb.manaus.ischia.LayUniverse
import cz.fb.manaus.reactor.betting.validator.Validator
import cz.fb.manaus.reactor.betting.validator.common.AccountMoneyValidator
import cz.fb.manaus.spring.ManausProfiles.PRODUCTION
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@BackUniverse
@LayUniverse
@Component
@Profile(PRODUCTION)
object AccountMoneyValidator : Validator by AccountMoneyValidator(25.0)

package cz.fb.manaus.ischia.validator

import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.validator.Validator
import cz.fb.manaus.reactor.betting.validator.common.AccountMoneyValidator
import cz.fb.manaus.spring.ManausProfiles.PRODUCTION
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@BackLoserBet
@LayLoserBet
@Component
@Profile(PRODUCTION)
object AccountMoneyValidator : Validator by AccountMoneyValidator(25.0)

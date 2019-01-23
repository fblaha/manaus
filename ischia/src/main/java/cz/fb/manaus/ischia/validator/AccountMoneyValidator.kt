package cz.fb.manaus.ischia.validator

import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.validator.common.AbstractAccountMoneyValidator
import cz.fb.manaus.spring.ManausProfiles.PRODUCTION
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@BackLoserBet
@LayLoserBet
@Component
@Profile(PRODUCTION)
object AccountMoneyValidator : AbstractAccountMoneyValidator(25.0)

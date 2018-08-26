package cz.fb.manaus.manila.validator

import cz.fb.manaus.manila.ManilaBet
import cz.fb.manaus.reactor.betting.validator.common.AbstractAccountMoneyValidator
import cz.fb.manaus.spring.ManausProfiles.PRODUCTION
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@ManilaBet
@Component
@Profile(PRODUCTION)
class AccountMoneyValidator : AbstractAccountMoneyValidator(25.0)

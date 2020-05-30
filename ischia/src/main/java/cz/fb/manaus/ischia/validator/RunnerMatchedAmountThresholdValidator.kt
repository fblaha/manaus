package cz.fb.manaus.ischia.validator

import cz.fb.manaus.ischia.BackUniverse
import cz.fb.manaus.ischia.LayUniverse
import cz.fb.manaus.reactor.betting.validator.Validator
import cz.fb.manaus.reactor.betting.validator.common.RunnerMatchedAmountThresholdValidator
import org.springframework.stereotype.Component

@BackUniverse
@LayUniverse
@Component
object RunnerMatchedAmountThresholdValidator :
        Validator by RunnerMatchedAmountThresholdValidator(2.0)

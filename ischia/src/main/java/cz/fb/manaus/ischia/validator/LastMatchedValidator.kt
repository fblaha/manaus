package cz.fb.manaus.ischia.validator

import cz.fb.manaus.ischia.BackUniverse
import cz.fb.manaus.ischia.LayUniverse
import cz.fb.manaus.reactor.betting.validator.Validator
import cz.fb.manaus.reactor.betting.validator.common.LastMatchedValidator
import org.springframework.stereotype.Component

@BackUniverse
@LayUniverse
@Component
object LastMatchedValidator : Validator by LastMatchedValidator(true)

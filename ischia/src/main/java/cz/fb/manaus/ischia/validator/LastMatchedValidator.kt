package cz.fb.manaus.ischia.validator

import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.validator.Validator
import cz.fb.manaus.reactor.betting.validator.common.BaseLastMatchedValidator
import org.springframework.stereotype.Component

@BackLoserBet
@LayLoserBet
@Component
object LastMatchedValidator : Validator by BaseLastMatchedValidator(true)

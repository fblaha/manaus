package cz.fb.manaus.manila.validator.update

import cz.fb.manaus.manila.ManilaBet
import cz.fb.manaus.reactor.betting.validator.Validator
import cz.fb.manaus.reactor.betting.validator.common.update.TooCloseUpdateEpsilonValidator
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@ManilaBet
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
object TooCloseUpdateEpsilonValidator : Validator by TooCloseUpdateEpsilonValidator(0.02)

package cz.fb.manaus.manila.validator.update

import cz.fb.manaus.manila.ManilaBet
import cz.fb.manaus.reactor.betting.validator.common.update.AbstractTooCloseUpdateEpsilonValidator
import org.springframework.context.annotation.Profile
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@ManilaBet
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Profile("matchbook")
class TooCloseUpdateEpsilonValidator : AbstractTooCloseUpdateEpsilonValidator(0.02)

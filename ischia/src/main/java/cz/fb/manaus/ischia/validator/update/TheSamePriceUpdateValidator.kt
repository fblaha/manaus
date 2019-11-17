package cz.fb.manaus.ischia.validator.update

import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.validator.common.update.AbstractTooCloseUpdateValidator
import cz.fb.manaus.reactor.rounding.RoundingService
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@BackLoserBet
@LayLoserBet
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class TheSamePriceUpdateValidator(roundingService: RoundingService)
    : AbstractTooCloseUpdateValidator(emptySet(), roundingService) {

    override val isDowngradeAccepting: Boolean = false

}

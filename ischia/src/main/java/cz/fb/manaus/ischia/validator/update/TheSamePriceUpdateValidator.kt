package cz.fb.manaus.ischia.validator.update

import cz.fb.manaus.core.provider.ProviderTag.PriceShiftFixedStep
import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.validator.common.update.AbstractTooCloseUpdateValidator
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@BackLoserBet
@LayLoserBet
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
object TheSamePriceUpdateValidator : AbstractTooCloseUpdateValidator(emptySet()) {

    override val tags get() = setOf(PriceShiftFixedStep)

    override val isDowngradeAccepting: Boolean = false
}

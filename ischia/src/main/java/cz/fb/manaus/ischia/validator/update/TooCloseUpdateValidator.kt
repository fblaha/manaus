package cz.fb.manaus.ischia.validator.update

import cz.fb.manaus.core.provider.ProviderTag.PriceShiftFixedStep
import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.validator.common.update.AbstractTooCloseUpdateValidator
import org.springframework.stereotype.Component

@BackLoserBet
@LayLoserBet
@Component
object TooCloseUpdateValidator : AbstractTooCloseUpdateValidator(setOf(-1, 1)) {

    override val tags get() = setOf(PriceShiftFixedStep)

}

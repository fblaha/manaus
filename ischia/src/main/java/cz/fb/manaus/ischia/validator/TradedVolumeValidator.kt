package cz.fb.manaus.ischia.validator

import cz.fb.manaus.core.provider.ProviderTag.TradedVolume
import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator
import org.springframework.stereotype.Component

@BackLoserBet
@LayLoserBet
@Component
object TradedVolumeValidator : Validator {

    override val tags get() = setOf(TradedVolume)

    override fun validate(event: BetEvent): ValidationResult {
        return if (event.metrics.actualTradedVolume!!.volume.size >= 3) ValidationResult.OK else ValidationResult.DROP
    }
}

package cz.fb.manaus.ischia.validator

import cz.fb.manaus.core.provider.ProviderTag.TradedVolume
import cz.fb.manaus.ischia.BackUniverse
import cz.fb.manaus.ischia.LayUniverse
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator
import org.springframework.stereotype.Component

@BackUniverse
@LayUniverse
@Component
object TradedVolumeValidator : Validator {

    override val tags get() = setOf(TradedVolume)

    override fun validate(event: BetEvent): ValidationResult {
        val actualTradedVolume = event.metrics.actualTradedVolume ?: error("no vol")
        return if (actualTradedVolume.volume.size >= 3) ValidationResult.OK else ValidationResult.DROP
    }
}

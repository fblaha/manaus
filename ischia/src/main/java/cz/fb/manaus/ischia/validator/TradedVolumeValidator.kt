package cz.fb.manaus.ischia.validator

import cz.fb.manaus.core.provider.ProviderCapability
import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator
import org.springframework.stereotype.Component

@BackLoserBet
@LayLoserBet
@Component
object TradedVolumeValidator : Validator {

    override val requiredCapabilities: Set<ProviderCapability>
        get() = setOf(ProviderCapability.TradedVolume)

    override fun validate(context: BetContext): ValidationResult {
        return ValidationResult.of(context.actualTradedVolume!!.volume.size >= 3)
    }
}

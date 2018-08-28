package cz.fb.manaus.ischia.validator

import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@BackLoserBet
@LayLoserBet
@Component
@Profile("betfair")
class TradedVolumeValidator : Validator {

    override fun validate(context: BetContext): ValidationResult {
        return ValidationResult.of(context.actualTradedVolume.get().volume.size >= 3)
    }
}

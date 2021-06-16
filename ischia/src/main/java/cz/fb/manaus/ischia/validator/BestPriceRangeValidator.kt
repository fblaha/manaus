package cz.fb.manaus.ischia.validator

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.ischia.BackUniverse
import cz.fb.manaus.ischia.LayUniverse
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.ValidationResult.DROP
import cz.fb.manaus.reactor.betting.validator.ValidationResult.OK
import cz.fb.manaus.reactor.betting.validator.Validator
import org.springframework.stereotype.Component

@LayUniverse
@BackUniverse
@Component
object BestPriceRangeValidator : Validator {

    override fun validate(event: BetEvent): ValidationResult {
        val bestLay = event.runnerPrices.by(Side.LAY).bestPrice
        val bestBack = event.runnerPrices.by(Side.BACK).bestPrice
        return if (bestBack != null && bestLay != null) {
            if (listOf(bestBack.price, bestLay.price).all { it in 1.3..6.0 }) OK else DROP
        } else {
            DROP
        }
    }
}

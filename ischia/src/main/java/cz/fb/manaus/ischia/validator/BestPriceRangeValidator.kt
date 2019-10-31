package cz.fb.manaus.ischia.validator

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.ValidationResult.ACCEPT
import cz.fb.manaus.reactor.betting.validator.ValidationResult.REJECT
import cz.fb.manaus.reactor.betting.validator.Validator
import org.springframework.stereotype.Component

@LayLoserBet
@BackLoserBet
@Component
object BestPriceRangeValidator : Validator<BetEvent> {

    override fun validate(event: BetEvent): ValidationResult {
        val bestLay = event.runnerPrices.getHomogeneous(Side.LAY).bestPrice
        val bestBack = event.runnerPrices.getHomogeneous(Side.BACK).bestPrice
        return if (bestBack != null && bestLay != null) {
            if (listOf(bestBack.price, bestLay.price).all { it in 1.3..6.0 }) ACCEPT else REJECT
        } else {
            REJECT
        }
    }
}

package cz.fb.manaus.ischia.validator

import com.google.common.collect.Range
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.ValidationResult.REJECT
import cz.fb.manaus.reactor.betting.validator.Validator
import org.springframework.stereotype.Component

@LayLoserBet
@BackLoserBet
@Component
class BestPriceRangeValidator : Validator {

    override fun validate(context: BetContext): ValidationResult {
        val bestLay = context.runnerPrices.getHomogeneous(Side.LAY).bestPrice
        val bestBack = context.runnerPrices.getHomogeneous(Side.BACK).bestPrice
        return if (bestBack.isPresent && bestLay.isPresent) {
            val backPrice = bestBack.get().price
            val layPrice = bestLay.get().price
            ValidationResult.of(RANGE.containsAll(listOf(backPrice, layPrice)))
        } else {
            REJECT
        }
    }

    companion object {
        val RANGE = Range.closed(1.3, 6.0)
    }

}

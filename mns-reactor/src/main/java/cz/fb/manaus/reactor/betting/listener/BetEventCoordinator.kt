package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetCommand
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.PriceAdviser
import cz.fb.manaus.reactor.betting.validator.ValidationCoordinator
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import io.micrometer.core.instrument.Metrics

class BetEventCoordinator(
        override val side: Side,
        private val validationCoordinator: ValidationCoordinator,
        private val priceAdviser: PriceAdviser
) : BetEventListener {

    override fun onBetEvent(event: BetEvent): BetCommand? {
        when (validationCoordinator.validatePrePrice(event)) {
            ValidationResult.DROP -> return cancel(event)
            ValidationResult.OK -> {
                val newPrice = priceAdviser.getNewPrice(event) ?: return cancel(event)
                event.newPrice = newPrice.price
                if (!event.isOldMatched) {
                    return when (validationCoordinator.validatePrice(event)) {
                        ValidationResult.DROP -> cancel(event)
                        ValidationResult.OK -> event.placeOrUpdate(newPrice.proposers)
                        else -> null
                    }
                }
                return null
            }
            ValidationResult.NOP -> return null
        }
    }

    private fun cancel(event: BetEvent): BetCommand? {
        return event.cancel?.let {
            Metrics.counter("mns_bet_cancel").increment()
            it
        }
    }

}
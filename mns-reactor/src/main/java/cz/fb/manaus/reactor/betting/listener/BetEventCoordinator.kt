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
                val priceEvent = event.copy(proposedPrice = newPrice.price)
                if (!event.isOldMatched) {
                    return when (validationCoordinator.validatePrice(priceEvent)) {
                        ValidationResult.DROP -> cancel(priceEvent)
                        ValidationResult.OK -> priceEvent.placeOrUpdate(newPrice.proposers)
                        else -> null
                    }
                }
                return null
            }
            ValidationResult.NOP -> return null
        }
    }

    private fun cancel(event: BetEvent): BetCommand? {
        return if (event.cancelable) {
            Metrics.counter("mns_bet_cancel").increment()
            event.cancel
        } else {
            null
        }
    }
}
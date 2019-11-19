package cz.fb.manaus.reactor.betting.listener

import com.codahale.metrics.MetricRegistry
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetCommand
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.PriceAdviser
import cz.fb.manaus.reactor.betting.validator.ValidationCoordinator
import cz.fb.manaus.reactor.betting.validator.ValidationResult

class BetEventCoordinator(
        override val side: Side,
        private val validationCoordinator: ValidationCoordinator,
        private val priceAdviser: PriceAdviser,
        private val metricRegistry: MetricRegistry
) : BetEventListener {

    override fun onBetEvent(event: BetEvent): BetCommand? {
        when (validationCoordinator.validatePrePrice(event)) {
            ValidationResult.DROP -> return cancel(event)
            ValidationResult.OK -> {
                val newPrice = priceAdviser.getNewPrice(event) ?: return cancel(event)
                event.newPrice = newPrice.price
                event.proposers = newPrice.proposers

                if (!event.isOldMatched) {
                    return when (validationCoordinator.validatePrice(event)) {
                        ValidationResult.DROP -> cancel(event)
                        ValidationResult.OK -> event.placeOrUpdate
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
            metricRegistry.counter("bet.cancel").inc()
            it
        }
    }

}
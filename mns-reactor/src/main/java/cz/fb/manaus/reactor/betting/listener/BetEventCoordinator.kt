package cz.fb.manaus.reactor.betting.listener

import com.codahale.metrics.MetricRegistry
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetCommand
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.PriceAdviser
import cz.fb.manaus.reactor.betting.validator.ValidationCoordinator
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import org.springframework.beans.factory.annotation.Autowired

class BetEventCoordinator(
        override val side: Side,
        private val validationCoordinator: ValidationCoordinator,
        private val priceAdviser: PriceAdviser
) : BetEventListener {

    @Autowired
    private lateinit var metricRegistry: MetricRegistry

    override fun onBetEvent(event: BetEvent): List<BetCommand> {
        val collector = mutableListOf<BetCommand>()
        val prePriceValidation = validationCoordinator.validatePrePrice(event)
        cancelOnDrop(prePriceValidation, event, collector)
        if (prePriceValidation == ValidationResult.OK) {
            val newPrice = priceAdviser.getNewPrice(event)
            if (newPrice == null) {
                cancel(event, collector)
                return collector.toList()
            }
            event.newPrice = newPrice.price
            event.proposers = newPrice.proposers

            if (!event.isOldMatched) {
                val priceValidation = validationCoordinator.validatePrice(event)
                cancelOnDrop(priceValidation, event, collector)
                if (priceValidation == ValidationResult.OK) {
                    check(prePriceValidation == ValidationResult.OK && priceValidation == ValidationResult.OK)
                    collector.add(event.placeOrUpdate)
                }
            }
        }
        return collector.toList()
    }

    private fun cancelOnDrop(prePriceValidation: ValidationResult, event: BetEvent, collector: MutableList<BetCommand>) {
        if (prePriceValidation == ValidationResult.DROP) {
            cancel(event, collector)
        }
    }

    private fun cancel(event: BetEvent, collector: MutableList<BetCommand>) {
        event.cancel?.let {
            metricRegistry.counter("bet.cancel").inc()
            collector.add(it)
        }
    }

}
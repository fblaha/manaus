package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.reactor.betting.BetCommand
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.PriceAdviser
import cz.fb.manaus.reactor.betting.validator.ValidationCoordinator
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import org.springframework.beans.factory.annotation.Autowired

class BetEventCoordinator(
        private val validationCoordinator: ValidationCoordinator,
        private val priceAdviser: PriceAdviser
) : BetEventListener {

    @Autowired
    private lateinit var betCommandIssuer: BetCommandIssuer

    override fun onBetEvent(event: BetEvent): List<BetCommand> {
        val collector = mutableListOf<BetCommand>()
        val prePriceValidation = validationCoordinator.validatePrePrice(event)
        cancelOnDrop(prePriceValidation, event.oldBet, collector)
        if (prePriceValidation == ValidationResult.OK) {
            val newPrice = priceAdviser.getNewPrice(event)
            if (newPrice == null) {
                betCommandIssuer.tryCancel(event.oldBet)?.let { collector.add(it) }
                return collector.toList()
            }
            event.newPrice = newPrice.price
            event.proposers = newPrice.proposers

            if (!event.isOldMatched) {
                val priceValidation = validationCoordinator.validatePrice(event)
                cancelOnDrop(priceValidation, event.oldBet, collector)
                if (priceValidation == ValidationResult.OK) {
                    check(prePriceValidation == ValidationResult.OK && priceValidation == ValidationResult.OK)
                    collector.add(betCommandIssuer.placeOrUpdate(event))
                }
            }
        }
        return collector.toList()
    }

    private fun cancelOnDrop(prePriceValidation: ValidationResult, oldBet: Bet?, collector: MutableList<BetCommand>) {
        if (prePriceValidation == ValidationResult.DROP) {
            betCommandIssuer.tryCancel(oldBet)?.let { collector.add(it) }
        }
    }

}
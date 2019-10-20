package cz.fb.manaus.ischia.proposer

import cz.fb.manaus.core.provider.ProviderCapability
import cz.fb.manaus.core.provider.ProviderCapability.LastMatchedPrice
import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.price.PriceService
import org.springframework.stereotype.Component


@Component
@BackLoserBet
@LayLoserBet
class LastMatchedProposer(private val priceService: PriceService) : PriceProposer {

    override val requiredCapabilities: Set<ProviderCapability>
        get() = setOf(LastMatchedPrice)

    override fun validate(context: BetContext): ValidationResult {
        val lastMatchedPrice = context.runnerPrices.lastMatchedPrice
        return ValidationResult.of(lastMatchedPrice != null)
    }

    override fun getProposedPrice(context: BetContext): Double {
        val lastMatchedPrice = context.runnerPrices.lastMatchedPrice!!
        return priceService.downgrade(lastMatchedPrice,
                0.01, context.side)
    }

}

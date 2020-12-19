package cz.fb.manaus.ischia.proposer

import cz.fb.manaus.core.provider.ProviderTag.LastMatchedPrice
import cz.fb.manaus.ischia.BackUniverse
import cz.fb.manaus.ischia.LayUniverse
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.price.Pricing
import org.springframework.stereotype.Component


@Component
@BackUniverse
@LayUniverse
object LastMatchedProposer : PriceProposer {

    override val tags get() = setOf(LastMatchedPrice)

    override fun validate(event: BetEvent): ValidationResult {
        val lastMatchedPrice = event.runnerPrices.lastMatchedPrice
        return if (lastMatchedPrice != null) ValidationResult.OK else ValidationResult.DROP
    }

    override fun getProposedPrice(event: BetEvent): Double {
        val lastMatchedPrice = event.runnerPrices.lastMatchedPrice!!
        return Pricing.downgrade(
                lastMatchedPrice,
                0.01, event.side
        )
    }

}

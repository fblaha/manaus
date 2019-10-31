package cz.fb.manaus.ischia.proposer

import cz.fb.manaus.core.provider.ProviderTag.LastMatchedPrice
import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.price.PriceService
import org.springframework.stereotype.Component


@Component
@BackLoserBet
@LayLoserBet
class LastMatchedProposer(private val priceService: PriceService) : PriceProposer {

    override val tags get() = setOf(LastMatchedPrice)

    override fun validate(event: BetEvent): ValidationResult {
        val lastMatchedPrice = event.runnerPrices.lastMatchedPrice
        return if (lastMatchedPrice != null) ValidationResult.ACCEPT else ValidationResult.REJECT
    }

    override fun getProposedPrice(event: BetEvent): Double {
        val lastMatchedPrice = event.runnerPrices.lastMatchedPrice!!
        return priceService.downgrade(lastMatchedPrice,
                0.01, event.side)
    }

}

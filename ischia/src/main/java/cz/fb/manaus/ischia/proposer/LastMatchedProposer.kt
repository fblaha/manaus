package cz.fb.manaus.ischia.proposer

import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.price.PriceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.Optional.ofNullable


@Component
@BackLoserBet
@LayLoserBet
@Profile("betfair")
class LastMatchedProposer : PriceProposer {

    @Autowired
    private lateinit var priceService: PriceService

    override fun validate(context: BetContext): ValidationResult {
        val lastMatchedPrice = ofNullable(context.runnerPrices.lastMatchedPrice)
        return ValidationResult.of(lastMatchedPrice.isPresent)
    }

    override fun getProposedPrice(context: BetContext): Double {
        val lastMatchedPrice = context.runnerPrices.lastMatchedPrice
        return priceService.downgrade(lastMatchedPrice,
                TradedVolumeProposer.REDUCTION_RATE, context.side)
    }

}

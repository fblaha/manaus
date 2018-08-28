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
import java.util.*


@Component
@BackLoserBet
@LayLoserBet
@Profile("betfair")
class TradedVolumeProposer : PriceProposer {
    @Autowired
    private val priceService: PriceService? = null

    override fun validate(context: BetContext): ValidationResult {
        val tradedVolume = context.actualTradedVolume.get()
        return if (tradedVolume.volume.size == 0 || tradedVolume.weightedMean.asDouble > 100) {
            ValidationResult.REJECT
        } else {
            super.validate(context)
        }
    }

    override fun getProposedPrice(context: BetContext): OptionalDouble {
        val weightedMean = context.actualTradedVolume.get().weightedMean.asDouble
        return OptionalDouble.of(priceService!!.downgrade(weightedMean, REDUCTION_RATE,
                context.side))
    }

    companion object {
        const val REDUCTION_RATE = 0.01
    }

}

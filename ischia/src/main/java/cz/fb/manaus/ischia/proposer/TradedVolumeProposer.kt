package cz.fb.manaus.ischia.proposer

import cz.fb.manaus.core.provider.ProviderTag.TradedVolume
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
class TradedVolumeProposer(private val priceService: PriceService) : PriceProposer {

    override val tags get() = setOf(TradedVolume)

    override fun validate(event: BetEvent): ValidationResult {
        val tradedVolume = event.metrics.actualTradedVolume!!
        return if (tradedVolume.volume.isEmpty() || tradedVolume.weightedMean!! > 100) {
            ValidationResult.REJECT
        } else {
            super.validate(event)
        }
    }

    override fun getProposedPrice(event: BetEvent): Double {
        val weightedMean = event.metrics.actualTradedVolume!!.weightedMean!!
        return priceService.downgrade(weightedMean, 0.01,
                event.side)
    }

}

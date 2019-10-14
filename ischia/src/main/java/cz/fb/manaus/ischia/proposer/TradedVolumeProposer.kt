package cz.fb.manaus.ischia.proposer

import cz.fb.manaus.core.provider.ProviderCapability
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
class TradedVolumeProposer(private val priceService: PriceService) : PriceProposer {

    override val requiredCapabilities: Set<ProviderCapability>
        get() = setOf(ProviderCapability.TradedVolume)

    override fun validate(context: BetContext): ValidationResult {
        val tradedVolume = context.actualTradedVolume!!
        return if (tradedVolume.volume.isEmpty() || tradedVolume.weightedMean!! > 100) {
            ValidationResult.REJECT
        } else {
            super.validate(context)
        }
    }

    override fun getProposedPrice(context: BetContext): Double {
        val weightedMean = context.actualTradedVolume!!.weightedMean!!
        return priceService.downgrade(weightedMean, 0.01,
                context.side)
    }

}

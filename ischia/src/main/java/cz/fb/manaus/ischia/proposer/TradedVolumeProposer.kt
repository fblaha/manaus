package cz.fb.manaus.ischia.proposer

import cz.fb.manaus.core.provider.ProviderTag.TradedVolume
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
object TradedVolumeProposer : PriceProposer {

    override val tags get() = setOf(TradedVolume)

    override fun validate(event: BetEvent): ValidationResult {
        val tradedVolume = event.metrics.actualTradedVolume ?: error("no vol")
        return if (tradedVolume.volume.isEmpty() || tradedVolume.weightedMean!! > 100) {
            ValidationResult.DROP
        } else {
            super.validate(event)
        }
    }

    override fun getProposedPrice(event: BetEvent): Double {
        val weightedMean = event.metrics.actualTradedVolume!!.weightedMean!!
        return Pricing.downgrade(
                weightedMean, 0.01,
                event.side
        )
    }

}

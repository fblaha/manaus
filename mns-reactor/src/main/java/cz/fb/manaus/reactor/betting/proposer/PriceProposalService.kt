package cz.fb.manaus.reactor.betting.proposer

import com.google.common.base.Preconditions
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.price.PriceService
import cz.fb.manaus.reactor.rounding.RoundingService
import org.springframework.stereotype.Service
import java.util.Objects.requireNonNull

@Service
class PriceProposalService(private val roundingService: RoundingService,
                           private val priceService: PriceService) {

    fun reducePrices(context: BetContext, proposers: List<PriceProposer>, side: Side): ProposedPrice {
        val prices = mutableListOf<ProposedPrice>()
        Preconditions.checkState(!proposers.isEmpty())
        for (proposer in proposers) {
            val proposedPrice = proposer.getProposedPrice(context)
            if (proposer.isMandatory) {
                Preconditions.checkState(proposedPrice != null, proposer.javaClass)
            }
            if (proposedPrice != null) {
                prices.add(ProposedPrice(proposedPrice, proposer.name))
            }
        }
        return reduce(side, prices)
    }

    private fun reduce(side: Side, values: List<ProposedPrice>): ProposedPrice {
        val result: ProposedPrice = if (requireNonNull(side) === Side.BACK) {
            values.maxBy { it.price }!!
        } else {
            values.minBy { it.price }!!
        }

        val proposers = values
                .filter { Price.priceEq(it.price, result.price) }
                .flatMap { it.proposers }.toSet()
        return ProposedPrice(result.price, proposers)
    }
}

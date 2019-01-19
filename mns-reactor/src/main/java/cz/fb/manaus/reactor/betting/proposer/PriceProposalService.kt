package cz.fb.manaus.reactor.betting.proposer

import com.google.common.base.Preconditions
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetContext
import org.springframework.stereotype.Service

@Service
class PriceProposalService {

    fun reducePrices(context: BetContext, proposers: List<PriceProposer>): ProposedPrice<Double> {
        val prices = mutableListOf<ProposedPrice<Double>>()
        Preconditions.checkState(!proposers.isEmpty())
        for (proposer in proposers) {
            val proposedPrice = proposer.getProposedPrice(context)
            if (proposer.isMandatory) {
                Preconditions.checkState(proposedPrice != null, proposer.javaClass)
            }
            if (proposedPrice != null) {
                prices.add(ProposedPrice(proposedPrice, setOf(proposer.name)))
            }
        }
        return reduce(context.side, prices)
    }

    private fun reduce(side: Side, values: List<ProposedPrice<Double>>): ProposedPrice<Double> {
        val result: ProposedPrice<Double> = if (side === Side.BACK) {
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

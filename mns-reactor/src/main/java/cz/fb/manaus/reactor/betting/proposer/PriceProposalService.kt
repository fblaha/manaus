package cz.fb.manaus.reactor.betting.proposer

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetContext
import org.springframework.stereotype.Service

@Service
class PriceProposalService {

    fun reducePrices(context: BetContext, proposers: List<PriceProposer>): ProposedPrice<Double> {
        val prices = mutableListOf<ProposedPrice<Double>>()
        check(!proposers.isEmpty())
        for (proposer in proposers) {
            val proposedPrice = proposer.getProposedPrice(context)
            if (proposer.isMandatory) {
                check(proposedPrice != null) { proposer.javaClass }
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

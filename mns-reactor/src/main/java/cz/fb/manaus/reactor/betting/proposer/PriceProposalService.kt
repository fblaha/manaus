package cz.fb.manaus.reactor.betting.proposer

import cz.fb.manaus.core.makeName
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.priceEq
import cz.fb.manaus.reactor.betting.BetEvent
import org.springframework.stereotype.Service

@Service
object PriceProposalService {

    fun reducePrices(event: BetEvent, proposers: List<PriceProposer>): ProposedPrice<Double> {
        val prices = mutableListOf<ProposedPrice<Double>>()
        check(proposers.isNotEmpty())
        val provider = event.account.provider
        for (proposer in proposers.filter(provider::matches)) {
            val proposedPrice = proposer.getProposedPrice(event)
            if (proposer.isMandatory) {
                check(proposedPrice != null) { proposer.javaClass }
            }
            if (proposedPrice != null) {
                prices.add(ProposedPrice(proposedPrice, setOf(makeName(proposer))))
            }
        }
        return reduce(event.side, prices)
    }

    private fun reduce(side: Side, values: List<ProposedPrice<Double>>): ProposedPrice<Double> {
        val result: ProposedPrice<Double> = if (side == Side.BACK) {
            values.maxByOrNull { it.price } ?: error("empty")
        } else {
            values.minByOrNull { it.price } ?: error("empty")
        }

        val proposers = values
                .filter { it.price priceEq result.price }
                .flatMap { it.proposers }.toSet()
        return ProposedPrice(result.price, proposers)
    }
}

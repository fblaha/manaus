package cz.fb.manaus.reactor.betting.proposer

import com.google.common.base.Preconditions
import com.google.common.collect.Ordering
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.price.PriceService
import cz.fb.manaus.reactor.rounding.RoundingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import java.util.Comparator.comparingDouble
import java.util.Objects.requireNonNull

@Service
class PriceProposalService {
    @Autowired
    private lateinit var roundingService: RoundingService
    @Autowired
    private lateinit var priceService: PriceService

    fun reducePrices(context: BetContext, proposers: List<PriceProposer>, side: Side): ProposedPrice {
        val prices = LinkedList<ProposedPrice>()
        Preconditions.checkState(!proposers.isEmpty())
        for (proposer in proposers) {
            val proposedPrice = proposer.getProposedPrice(context)
            if (proposer.isMandatory) {
                Preconditions.checkState(proposedPrice.isPresent, proposer.javaClass)
            }
            if (proposedPrice.isPresent) {
                prices.add(ProposedPrice(proposedPrice.asDouble, proposer.name))
            }
        }
        return reduce(side, prices)
    }

    private fun reduce(side: Side, values: List<ProposedPrice>): ProposedPrice {
        val result: ProposedPrice = if (requireNonNull(side) === Side.BACK) {
            ORDERING.max(values)
        } else {
            ORDERING.min(values)
        }

        val proposers = values
                .filter { Price.priceEq(it.price, result.price) }
                .flatMap { it.proposers }.toSet()
        return ProposedPrice(result.price, proposers)
    }

    companion object {
        val ORDERING = Ordering.from(comparingDouble<ProposedPrice> { it.price })!!
    }

}

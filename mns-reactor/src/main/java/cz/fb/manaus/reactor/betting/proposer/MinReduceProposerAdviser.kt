package cz.fb.manaus.reactor.betting.proposer

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.reactor.betting.AmountAdviser
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.PriceAdviser
import cz.fb.manaus.reactor.rounding.RoundingService
import java.util.logging.Logger
import javax.annotation.PostConstruct
import kotlin.math.max

class MinReduceProposerAdviser(
        private val proposers: List<PriceProposer>,
        private val adviser: AmountAdviser,
        private val proposalService: PriceProposalService,
        private val roundingService: RoundingService
) : PriceAdviser {

    private val log = Logger.getLogger(MinReduceProposerAdviser::class.simpleName)

    override fun getNewPrice(betEvent: BetEvent): ProposedPrice<Price>? {
        val proposedPrice = proposalService.reducePrices(betEvent, proposers)
        val tagPredicate = betEvent.account.provider::matches
        val roundedPrice = roundingService.roundBet(proposedPrice.price, tagPredicate)

        return if (roundedPrice != null) {
            var amount = adviser.amount
            val counterBet = betEvent.counterBet
            if (counterBet != null && counterBet.matchedAmount > 0) {
                amount = counterBet.requestedPrice.amount
            }
            val minAmount = betEvent.account.provider.minAmount
            val price = Price(roundedPrice, max(amount, minAmount), betEvent.side)
            ProposedPrice(price, proposedPrice.proposers)
        } else {
            null
        }
    }

    @PostConstruct
    fun logConfig() {
        val proposerList = proposers.map { it.javaClass }
                .map { it.simpleName }.sorted().joinToString(",")
        log.info { "proposer coordinator class: '${this.javaClass.simpleName}', proposers: '$proposerList'" }
    }
}

package cz.fb.manaus.reactor.betting.proposer

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.reactor.betting.AmountAdviser
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.PriceAdviser
import cz.fb.manaus.reactor.rounding.RoundingService
import org.springframework.beans.factory.annotation.Autowired
import java.util.logging.Logger
import javax.annotation.PostConstruct
import kotlin.math.max

open class ProposerAdviser(private val proposers: List<PriceProposer>) : PriceAdviser {

    @Autowired
    private lateinit var adviser: AmountAdviser
    @Autowired
    private lateinit var proposalService: PriceProposalService
    @Autowired
    private lateinit var roundingService: RoundingService

    private val log = Logger.getLogger(ProposerAdviser::class.simpleName)

    override fun getNewPrice(betContext: BetContext): ProposedPrice<Price>? {
        val proposedPrice = proposalService.reducePrices(betContext, proposers)
        val capabilityPredicate = betContext.account.provider::hasCapabilities
        val roundedPrice = roundingService.roundBet(proposedPrice.price, capabilityPredicate)

        return if (roundedPrice != null) {
            var amount = adviser.amount
            val counterBet = betContext.counterBet
            if (counterBet != null && counterBet.matchedAmount > 0) {
                amount = counterBet.requestedPrice.amount
            }
            val minAmount = betContext.account.provider.minAmount
            val price = Price(roundedPrice, max(amount, minAmount), betContext.side)
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

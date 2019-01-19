package cz.fb.manaus.reactor.betting.proposer

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.provider.ExchangeProvider
import cz.fb.manaus.reactor.betting.AmountAdviser
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.PriceAdviser
import cz.fb.manaus.reactor.rounding.RoundingService
import org.springframework.beans.factory.annotation.Autowired
import java.util.logging.Level
import java.util.logging.Logger
import javax.annotation.PostConstruct

open class ProposerAdviser(private val proposers: List<PriceProposer>) : PriceAdviser {

    @Autowired
    private lateinit var adviser: AmountAdviser
    @Autowired
    private lateinit var provider: ExchangeProvider
    @Autowired
    private lateinit var proposalService: PriceProposalService
    @Autowired
    private lateinit var roundingService: RoundingService

    private val log = Logger.getLogger(ProposerAdviser::class.java.simpleName)

    override fun getNewPrice(betContext: BetContext): ProposedPrice<Price>? {
        val proposedPrice = proposalService.reducePrices(betContext, proposers)
        val roundedPrice = roundingService.roundBet(proposedPrice.price)

        return if (roundedPrice != null) {
            var amount = adviser.amount
            val counterBet = betContext.counterBet
            if (counterBet != null && counterBet.matchedAmount > 0) {
                amount = counterBet.requestedPrice.amount
            }
            val price = Price(roundedPrice, Math.max(amount, provider.minAmount), betContext.side)
            ProposedPrice(price, proposedPrice.proposers)
        } else {
            null
        }
    }

    @PostConstruct
    fun logConfig() {
        val proposerList = proposers.map { it.javaClass }
                .map { it.simpleName }.sorted().joinToString(",")
        log.log(Level.INFO, "Proposer coordinator class: ''{0}'', proposers: ''{1}''",
                arrayOf<Any>(this.javaClass.simpleName, proposerList))
    }
}

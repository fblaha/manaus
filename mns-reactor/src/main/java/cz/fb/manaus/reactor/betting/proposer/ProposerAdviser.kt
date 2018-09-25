package cz.fb.manaus.reactor.betting.proposer

import com.google.common.base.Joiner
import cz.fb.manaus.core.model.BetAction
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

    override fun getNewPrice(betContext: BetContext): Price? {
        val proposedPrice = reducePrices(betContext)
        return if (proposedPrice != null) {
            var amount = adviser.amount
            val counterBet = betContext.counterBet
            if (counterBet != null && counterBet.matchedAmount > 0) {
                amount = counterBet.requestedPrice.amount
            }
            Price(proposedPrice,
                    Math.max(amount, provider.minAmount), betContext.side)
        } else {
            null
        }
    }

    private fun reducePrices(context: BetContext): Double? {
        val proposedPrice = proposalService.reducePrices(context, proposers, context.side)
        val rounded = roundingService.roundBet(proposedPrice.price)
        if (rounded != null) {
            context.properties[BetAction.PROPOSER_PROP] = Joiner.on(',').join(proposedPrice.proposers)
        }
        return rounded
    }

    @PostConstruct
    fun logConfig() {
        val proposerList = proposers.map { it.javaClass }
                .map { it.simpleName }.sorted().joinToString(",")
        log.log(Level.INFO, "Proposer coordinator class: ''{0}'', proposers: ''{1}''",
                arrayOf<Any>(this.javaClass.simpleName, proposerList))
    }

    companion object {
        private val log = Logger.getLogger(ProposerAdviser::class.java.simpleName)
    }
}

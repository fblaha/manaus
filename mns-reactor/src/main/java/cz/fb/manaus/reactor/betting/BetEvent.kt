package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.model.*
import cz.fb.manaus.reactor.price.Fairness
import java.time.Instant
import java.util.logging.Logger

data class BetEvent(
        val sideSelection: SideSelection,
        val market: Market,
        val marketPrices: List<RunnerPrices>,
        val coverage: Map<SideSelection, Bet>,
        val account: Account,
        val metrics: BetMetrics,
        val proposedPrice: Price? = null
) {

    private val log = Logger.getLogger(BetEvent::class.simpleName)

    val side: Side = sideSelection.side
    val runner: Runner = market.getRunner(sideSelection.selectionId)
    val runnerPrices: RunnerPrices = marketPrices.first { it.selectionId == sideSelection.selectionId }
    val oldBet: Bet? = coverage[sideSelection]
    val counterBet: Bet? = coverage[sideSelection.oppositeSide]
    val isOldMatched: Boolean = oldBet?.isMatched == true
    val betActionType = if (oldBet == null) BetActionType.PLACE else BetActionType.UPDATE
    val cancelable = oldBet != null && !oldBet.isMatched

    init {
        if (proposedPrice != null) {
            val newSide = proposedPrice.side
            check(sideSelection.side == newSide)
            if (oldBet != null) {
                val oldSide = oldBet.requestedPrice.side
                check(oldSide == newSide)
            }
            if (counterBet != null) {
                val otherSide = counterBet.requestedPrice.side
                check(otherSide == newSide.opposite)
            }
        }
    }

    fun betAction(proposers: Set<String>): BetAction {
        return BetAction(
                selectionId = sideSelection.selectionId,
                price = proposedPrice!!,
                id = 0,
                time = Instant.now(),
                marketId = market.id,
                runnerPrices = marketPrices,
                betActionType = betActionType,
                chargeGrowth = metrics.chargeGrowthForecast,
                proposers = proposers
        )
    }

    // TODO not used
    val simulatedBet: RealizedBet get() = simulate(betAction(emptySet()), market)

    fun placeOrUpdate(proposers: Set<String>): BetCommand {
        val action = betAction(proposers)
        val newPrice = proposedPrice!!

        log.info { "bet $betActionType action '$action'" }
        return if (betActionType == BetActionType.PLACE) {
            val bet = Bet(
                    marketId = market.id,
                    placedDate = Instant.now(),
                    selectionId = runnerPrices.selectionId,
                    requestedPrice = newPrice
            )
            BetCommand(bet, action)
        } else {
            val old = oldBet ?: error("no old bet")
            BetCommand(old replacePrice newPrice.price, action)
        }
    }

    val cancel: BetCommand
        get() = BetCommand(oldBet ?: error("no old bet"), null)
}


fun createBetEvent(
        sideSelection: SideSelection,
        snapshot: MarketSnapshot,
        account: Account,
        fairness: Fairness,
        forecast: Double? = null
): BetEvent {
    val metrics = BetMetrics(
            chargeGrowthForecast = forecast,
            fairness = fairness,
            actualTradedVolume = snapshot.tradedVolume?.get(key = sideSelection.selectionId)
    )
    return BetEvent(
            sideSelection = sideSelection,
            market = snapshot.market,
            marketPrices = snapshot.runnerPrices,
            account = account,
            coverage = snapshot.coverage,
            metrics = metrics
    )
}

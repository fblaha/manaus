package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.model.*
import cz.fb.manaus.reactor.price.Fairness
import java.time.Instant
import java.util.logging.Logger

data class BetEvent(
        val sideSelection: SideSelection,
        val market: Market,
        val marketPrices: List<RunnerPrices>,
        val coverage: Map<SideSelection, TrackedBet>,
        val account: Account,
        val metrics: BetMetrics,
        val proposedPrice: Price? = null
) {

    private val log = Logger.getLogger(BetEvent::class.simpleName)

    val side: Side = sideSelection.side
    val runner: Runner = market.getRunner(sideSelection.selectionId)
    val runnerPrices: RunnerPrices = marketPrices.first { it.selectionId == sideSelection.selectionId }
    val oldBet: TrackedBet? = coverage[sideSelection]
    val counterBet: TrackedBet? = coverage[sideSelection.oppositeSide]
    val isOldMatched: Boolean = oldBet?.remote?.isMatched == true
    val actionType = if (oldBet == null) BetActionType.PLACE else BetActionType.UPDATE
    val cancelable = oldBet != null && !oldBet.remote.isMatched

    init {
        if (proposedPrice != null) {
            val newSide = proposedPrice.side
            check(sideSelection.side == newSide)
            if (oldBet != null) {
                val oldSide = oldBet.remote.requestedPrice.side
                check(oldSide == newSide)
            }
            if (counterBet != null) {
                val otherSide = counterBet.remote.requestedPrice.side
                check(otherSide == newSide.opposite)
            }
        }
    }

    fun betAction(proposers: Set<String>): BetAction {
        return BetAction(
                selectionId = sideSelection.selectionId,
                price = proposedPrice!!,
                id = "",
                time = Instant.now(),
                marketId = market.id,
                runnerPrices = marketPrices,
                betActionType = actionType,
                version = oldBet?.local?.let { it.version + 1 } ?: 1,
                chargeGrowth = metrics.chargeGrowthForecast,
                proposers = proposers
        )
    }

    // TODO not used
    val simulatedBet: RealizedBet get() = simulate(betAction(emptySet()), market)

    fun placeOrUpdate(proposers: Set<String>): BetCommand {
        val action = betAction(proposers)
        val newPrice = proposedPrice!!

        log.info { "bet $actionType action '$action'" }
        return if (actionType == BetActionType.PLACE) {
            val bet = TrackedBet(
                    remote = Bet(
                            marketId = market.id,
                            placedDate = Instant.now(),
                            selectionId = runnerPrices.selectionId,
                            requestedPrice = newPrice,
                    ),
                    local = action
            )
            BetCommand(bet)
        } else {
            val old = oldBet ?: error("no old bet")
            val bet = old replacePrice newPrice.price
            BetCommand(bet.copy(local = action))
        }
    }

    val cancel: BetCommand
        get() {
            val bet = oldBet ?: error("no old bet")
            return BetCommand(bet.copy(local = null))
        }
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

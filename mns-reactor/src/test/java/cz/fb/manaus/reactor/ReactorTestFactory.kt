package cz.fb.manaus.reactor

import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.provider.ExchangeProvider
import cz.fb.manaus.core.test.CoreTestFactory
import cz.fb.manaus.core.test.ModelFactory
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.BetContextFactory
import cz.fb.manaus.reactor.price.Fairness
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator
import cz.fb.manaus.reactor.price.PriceService
import cz.fb.manaus.reactor.rounding.RoundingService
import org.apache.commons.lang3.time.DateUtils.addHours
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.Optional.empty
import java.util.Optional.of


@Component
class ReactorTestFactory {

    @Autowired
    private lateinit var roundingService: RoundingService
    @Autowired
    private lateinit var calculator: FairnessPolynomialCalculator
    @Autowired
    private lateinit var priceService: PriceService
    @Autowired
    private lateinit var contextFactory: BetContextFactory
    @Autowired
    private lateinit var provider: ExchangeProvider

    fun newUpdateBetContext(marketPrices: MarketPrices, side: Side): BetContext {
        val oldBet = newBet(Price(5.0, 5.0, side))
        return newBetContext(side, marketPrices, of(oldBet)).withNewPrice(oldBet.requestedPrice)
    }

    fun newBetContext(side: Side, marketPrices: MarketPrices, oldBet: Optional<Bet>): BetContext {
        val fairness = Fairness(OptionalDouble.of(0.9), OptionalDouble.of(1.1))

        val bets = LinkedList<Bet>()
        oldBet.ifPresent { bet -> bets.add(bet) }
        val snapshot = MarketSnapshot.from(marketPrices, bets, empty<Map<Long, TradedVolume>>())

        return contextFactory.create(side, CoreTestFactory.HOME, snapshot, fairness,
                empty<AccountMoney>(), emptySet())
    }

    fun createContext(side: Side, bestBack: Double, bestLay: Double): BetContext {
        val marketPrices = createMarket(bestBack, bestLay, 3.0, 1)
        val runnerPrices = marketPrices.runnerPrices.iterator().next()
        val selectionId = runnerPrices.selectionId
        val bestPrice = runnerPrices.getHomogeneous(side.opposite).bestPrice
        val bets = LinkedList<Bet>()
        if (bestPrice.isPresent) {
            val marketId = CoreTestFactory.MARKET_ID
            val price = bestPrice.get().price
            val requestedPrice = Price(price, provider.minAmount, side.opposite)
            val date = Instant.now().minus(2, ChronoUnit.HOURS)
            val counterBet = Bet(BET_ID, marketId, selectionId, requestedPrice,
                    Date.from(date), provider.minAmount)
            bets.add(counterBet)
        }
        val snapshot = MarketSnapshot.from(marketPrices, bets, empty<Map<Long, TradedVolume>>())
        return contextFactory.create(side, selectionId, snapshot,
                calculator.getFairness(marketPrices), empty<AccountMoney>(), emptySet())

    }

    fun createRP(unfairPrices: List<Double>): List<RunnerPrices> {
        val runnerPrices = LinkedList<RunnerPrices>()
        for (i in unfairPrices.indices) {
            val unfairPrice = unfairPrices[i]
            runnerPrices.add(newRP(i.toLong(), unfairPrice, 10.0))
        }
        return runnerPrices
    }

    @JvmOverloads
    fun newRP(selectionId: Long, bestBack: Double, bestLay: Double, lastMatchedPrice: Double? = null): RunnerPrices {
        var lastMatched = lastMatchedPrice
        if (lastMatched == null) {
            lastMatched = roundingService.roundBet((bestBack + bestLay) / 2)!!
        }
        val backBestPrice = Price(bestBack, 100.0, Side.BACK)
        val layBestPrice = Price(bestLay, 100.0, Side.LAY)
        return ModelFactory.newRunnerPrices(selectionId, listOf(
                backBestPrice,
                layBestPrice,
                roundingService.decrement(backBestPrice, 1)!!,
                roundingService.decrement(backBestPrice, 2)!!,
                roundingService.increment(layBestPrice, 1)!!,
                roundingService.increment(layBestPrice, 2)!!),
                100.0, lastMatched)
    }

    fun createMarket(betBack: Double, bestLay: Double, lastMatched: Double?, winnerCount: Int): MarketPrices {
        val market = createMarket()
        val home = newRP(CoreTestFactory.HOME, betBack, bestLay, lastMatched)
        val draw = newRP(CoreTestFactory.DRAW, betBack, bestLay, lastMatched)
        val away = newRP(CoreTestFactory.AWAY, betBack, bestLay, lastMatched)
        return ModelFactory.newPrices(winnerCount, market, listOf(home, draw, away), Date())
    }

    fun createMarket(downgradeFraction: Double, probabilities: List<Double>): MarketPrices {
        val market = createMarket()
        val runnerPrices = LinkedList<RunnerPrices>()
        for (i in probabilities.indices) {
            val fairPrice = 1 / probabilities[i]
            val backPrice = priceService.downgrade(fairPrice, downgradeFraction, Side.LAY)
            val backRounded = roundingService.roundBet(backPrice)
            val layPrice = priceService.downgrade(fairPrice, downgradeFraction, Side.BACK)
            val layRounded = roundingService.roundBet(layPrice)
            val selectionId = CoreTestFactory.HOME + i
            val lastMatched = roundingService.roundBet(fairPrice)
            runnerPrices.add(newRP(selectionId, backRounded!!, layRounded!!, lastMatched!!))
        }
        return ModelFactory.newPrices(1, market, runnerPrices, Date())
    }

    private fun createMarket(): Market {
        val event = ModelFactory.newEvent("1", "Vischya Liga", addHours(Date(), 2), CoreTestFactory.COUNTRY_CODE)
        event.id = "1"
        val market = CoreTestFactory.newTestMarket()
        market.event = event
        return market
    }

    companion object {
        const val BET_ID = "111156454"
        fun newBet(oldOne: Price): Bet {
            return Bet(BET_ID, "1", CoreTestFactory.HOME, oldOne, Date(), 5.0)
        }
    }
}

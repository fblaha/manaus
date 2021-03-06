package cz.fb.manaus.core.model

import cz.fb.manaus.core.provider.ExchangeProvider
import cz.fb.manaus.core.provider.ProviderTag
import org.bson.types.ObjectId
import java.time.Instant
import java.time.temporal.ChronoUnit

const val SEL_HOME = 100L
const val SEL_AWAY = 200L
const val SEL_DRAW = 300L


val homeSettledBet: SettledBet = SettledBet(
        id = "1",
        selectionId = SEL_HOME,
        selectionName = "Banik Ostrava",
        profitAndLoss = 9.9,
        commission = null,
        placed = Instant.now().minus(24, ChronoUnit.HOURS).truncatedTo(ChronoUnit.SECONDS),
        matched = Instant.now().minus(16, ChronoUnit.HOURS).truncatedTo(ChronoUnit.SECONDS),
        settled = Instant.now().minus(8, ChronoUnit.HOURS).truncatedTo(ChronoUnit.SECONDS),
        price = Price(3.3, 3.0, Side.BACK)
)

val drawSettledBet: SettledBet = homeSettledBet.copy(
        selectionName = "The Draw",
        selectionId = SEL_DRAW,
        id = "2"
)

val homePrices = RunnerPrices(
        selectionId = SEL_HOME,
        matchedAmount = 100.0,
        lastMatchedPrice = 3.0,
        prices = listOf(
                Price(2.4, 100.0, Side.BACK),
                Price(2.5, 100.0, Side.BACK),
                Price(3.5, 100.0, Side.LAY),
                Price(3.7, 100.0, Side.LAY)
        )
)
val runnerPrices = listOf(
        homePrices,
        homePrices.copy(selectionId = SEL_DRAW),
        homePrices.copy(selectionId = SEL_AWAY)
)

val betAction = BetAction(
        id = ObjectId().toString(),
        marketId = "2",
        time = Instant.now().minus(25, ChronoUnit.HOURS).truncatedTo(ChronoUnit.SECONDS),
        selectionId = SEL_HOME,
        betId = "1",
        betActionType = BetActionType.PLACE,
        version = 1,
        runnerPrices = runnerPrices,
        price = Price(3.0, 3.0, Side.BACK),
        proposers = setOf("bestPrice")
)

val market = Market(
        id = "2",
        name = "Match Odds",
        inPlay = true,
        type = "match_odds",
        matchedAmount = 100.0,
        event = Event(
                id = "100",
                name = "Banik Ostrava vs Sparta Prague",
                openDate = Instant.now().plus(2, ChronoUnit.DAYS)
                        .truncatedTo(ChronoUnit.SECONDS),
                timezone = "CET",
                countryCode = "cz",
                venue = "bazaly"
        ),
        competition = Competition("100", "Czech League"),
        eventType = EventType("1000", "soccer"),
        runners = listOf(
                Runner(SEL_HOME, "Banik Ostrava", 0.0, 0),
                Runner(SEL_AWAY, "Sparta Prague", 0.0, 1),
                Runner(SEL_DRAW, "The Draw", 0.0, 2)
        )
)

val realizedBet = RealizedBet(homeSettledBet, betAction, market)

val tradedVolume = mapOf(
        SEL_HOME to TradedVolume(listOf(TradedAmount(3.0, 10.0))),
        SEL_DRAW to TradedVolume(listOf(TradedAmount(3.0, 10.0))),
        SEL_AWAY to TradedVolume(listOf(TradedAmount(3.0, 10.0)))
)

val accountMoney = AccountMoney(2000.0, 1000.0)

val BF_TAGS: Set<ProviderTag> = setOf(
        ProviderTag.VendorBetfair,
        ProviderTag.MatchedAmount,
        ProviderTag.TradedVolume,
        ProviderTag.LastMatchedPrice
)

val MB_TAGS: Set<ProviderTag> = setOf(
        ProviderTag.VendorMatchbook,
        ProviderTag.MatchedAmount,
)

val bfProvider = ExchangeProvider(
        name = "test_mb",
        minAmount = 2.0,
        minPrice = 1.001,
        commission = 0.02,
        tags = BF_TAGS
)

val mbProvider = ExchangeProvider(
        name = "test_bf",
        minAmount = 2.0,
        minPrice = 1.001,
        commission = 0.02,
        tags = MB_TAGS
)

val mbAccount = Account(mbProvider, accountMoney)
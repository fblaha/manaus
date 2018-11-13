package cz.fb.manaus.core.model

import java.time.Instant
import java.time.temporal.ChronoUnit

val homeSettledBet: SettledBet = SettledBet(
        id = "1",
        selectionId = SEL_HOME,
        selectionName = "Banik Ostrava",
        profitAndLoss = 9.9,
        matched = Instant.now().minus(16, ChronoUnit.HOURS),
        placed = Instant.now().minus(24, ChronoUnit.HOURS),
        settled = Instant.now().minus(8, ChronoUnit.HOURS),
        price = Price(3.3, 3.0, Side.BACK)
)

val drawSettledBet: SettledBet = homeSettledBet.copy(selectionName = "The Draw", selectionId = SEL_DRAW)

val homePrice = RunnerPrices(
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
        homePrice,
        homePrice.copy(selectionId = SEL_DRAW),
        homePrice.copy(selectionId = SEL_AWAY)
)

val betAction = BetAction(
        id = 0,
        marketID = "2",
        time = Instant.now().minus(25, ChronoUnit.HOURS),
        selectionID = SEL_HOME,
        betID = "1",
        betActionType = BetActionType.PLACE,
        runnerPrices = runnerPrices,
        price = Price(3.0, 3.0, Side.BACK),
        properties = mapOf("x" to "y")
)

val market = Market(id = "2",
        name = "Match Odds",
        inPlay = true,
        type = "match_odds",
        matchedAmount = 100.0,
        event = Event(
                id = "100",
                name = "Banik Ostrava vs Sparta Prague",
                openDate = Instant.now(),
                timezone = "CET",
                countryCode = "cz",
                venue = "bazaly"),
        competition = Competition("100", "Czech League"),
        eventType = EventType("1000", "soccer"),
        runners = listOf(
                Runner(SEL_HOME, "Banik Ostrava", 0.0, 0),
                Runner(SEL_AWAY, "Sparta Prague", 0.0, 1),
                Runner(SEL_DRAW, "The Draw", 0.0, 2)
        )
)

val realizedBet = RealizedBet(homeSettledBet, betAction, market)

fun RealizedBet.replacePrice(price: Price): RealizedBet {
    return this.copy(
            settledBet = this.settledBet.copy(price = price),
            betAction = this.betAction.copy(price = price)
    )
}

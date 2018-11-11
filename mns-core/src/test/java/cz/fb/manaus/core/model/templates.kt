package cz.fb.manaus.core.model

import java.time.Instant

val settledBet: SettledBet = SettledBet(
        id = "1",
        selectionId = SEL_HOME,
        selectionName = "Banik",
        profitAndLoss = 5.0,
        matched = Instant.now(),
        placed = Instant.now(),
        settled = Instant.now(),
        price = Price(3.0, 3.0, Side.BACK)
)

val runnerPrices = listOf(
        RunnerPrices(
                selectionId = SEL_HOME,
                matchedAmount = 100.0,
                lastMatchedPrice = 3.0,
                prices = listOf(
                        Price(3.0, 100.0, Side.BACK),
                        Price(3.5, 100.0, Side.LAY)
                )
        )
)

val betAction = BetAction(
        id = 0,
        marketID = "2",
        time = Instant.now(),
        selectionID = SEL_HOME,
        betID = null,
        betActionType = BetActionType.PLACE,
        runnerPrices = runnerPrices,
        price = Price(3.0, 3.0, Side.BACK),
        properties = mapOf("x" to "y")
)

val marketTemplate = Market(id = "2",
        name = "Match Odds",
        inPlay = true,
        type = "match_odds",
        matchedAmount = 100.0,
        event = Event(
                id = "100",
                name = "Ostrava vs Sparta",
                openDate = Instant.now(),
                timezone = "UTC",
                countryCode = "cz",
                venue = "letna"),
        competition = Competition("100", "Czech League"),
        eventType = EventType("1000", "soccer"),
        runners = listOf(
                Runner(SEL_HOME, "Banik Ostrava", 0.0, 0),
                Runner(SEL_AWAY, "Sparta Praha", 0.0, 0),
                Runner(SEL_DRAW, "The Draw", 0.0, 0)
        )
)

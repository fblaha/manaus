package cz.fb.manaus.core.test

import cz.fb.manaus.core.model.*
import java.util.*

interface ModelFactory {
    companion object {
        fun newAction(betActionType: BetActionType, actionDate: Date, price: Price,
                      market: Market, selectionId: Long): BetAction {
            val ba = BetAction()
            ba.betActionType = betActionType
            ba.actionDate = actionDate
            ba.price = price
            ba.market = market
            ba.selectionId = selectionId
            return ba
        }

        fun newEvent(id: String, name: String, openDate: Date, countryCode: String): Event {
            val event = Event()
            event.id = id
            event.name = name
            event.openDate = openDate
            event.countryCode = countryCode
            return event
        }

        fun newPrices(winnerCount: Int, market: Market, runnerPrices: Collection<RunnerPrices>, time: Date): MarketPrices {
            val mp = MarketPrices()
            mp.winnerCount = winnerCount
            mp.market = market
            mp.runnerPrices = runnerPrices
            mp.time = time
            return mp
        }

        fun newRunner(selectionId: Long, name: String, handicap: Double, sortPriority: Int): Runner {
            val runner = Runner()
            runner.selectionId = selectionId
            runner.name = name
            runner.handicap = handicap
            runner.sortPriority = sortPriority
            return runner
        }

        fun newRunnerPrices(selectionId: Long, prices: Collection<Price>, matched: Double?, lastMatchedPrice: Double?): RunnerPrices {
            val rp = RunnerPrices()
            rp.selectionId = selectionId
            rp.prices = prices
            rp.matchedAmount = matched
            rp.lastMatchedPrice = lastMatchedPrice
            return rp
        }

        fun newSettled(selectionId: Long, selectionName: String, profitAndLoss: Double, settled: Date, price: Price): SettledBet {
            val bet = SettledBet()
            bet.selectionId = selectionId
            bet.selectionName = selectionName
            bet.profitAndLoss = profitAndLoss
            bet.settled = settled
            bet.price = price
            return bet
        }
    }
}

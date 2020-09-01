package cz.fb.manaus.reactor.charge


import com.google.common.collect.Sets
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import org.springframework.stereotype.Component

@Component
object MarketChargeSimulator {

    fun getChargeMean(
        winnerCount: Int,
        commission: Double,
        probabilities: Map<Long, Double>,
        bets: Map<Long, List<Price>>
    ): Double {

        val defaultBets = bets.withDefault { emptyList() }
        val selections = probabilities.keys
        val winnerPowerSet = getWinnersPowerSet(winnerCount, selections)
        var chargeMean = 0.0
        for (winners in winnerPowerSet) {
            val probability = getProbability(winners, probabilities)
            var profit = 0.0
            for (selection in selections) {
                val isWinner = selection in winners
                val selectionBets = defaultBets.getValue(selection)
                profit += if (isWinner)
                    selectionBets.map { this.profitWinner(it) }.sum()
                else
                    selectionBets.map { this.profitLoser(it) }.sum()
            }
            if (profit > 0) {
                val charge = profit * commission
                chargeMean += probability * charge
            }
        }
        return chargeMean
    }

    private fun getProbability(winners: Set<Long>, probabilities: Map<Long, Double>): Double {
        return winners.map { probabilities[it]!! }.reduce { l, r -> l * r }
    }


    private fun profitWinner(price: Price): Double {
        val result = (price.price - 1) * price.amount
        return if (price.side == Side.BACK) {
            // DRAW (ENG/RUS) 03/06/16 11:08 Back 52500304 Standard 3.66 11/06/16 22:55 2 5.32
            // Fixtures 13 June Nadezhda Mogilev (W) v Zorka-BDU Minsk (W) / Match Odds / The Draw
            // Back	 	5.70	0.69	Won	 	3.24	2,133.79
            result
        } else {
            // DRAW (GAI/SIR) 11/06/16 8:52 Lay 52720548 Standard 3.62 11/06/16 17:59 2 -5.24
            // Fixtures 13 June Nadezhda Mogilev (W) v Zorka-BDU Minsk (W) / Match Odds / The Draw
            // Lay	 	5.10	2.00	Lost	(8.20)	 	2,125.59
            -result
        }
    }

    private fun profitLoser(price: Price): Double {
        return if (price.side == Side.BACK) {
            // DRAW (INT/AME) 10/06/16 23:32 Back 52710984 Standard 4.75 11/06/16 23:23 2 -2
            // Fixtures 13 June Madura Utd v Persiba Balikpapan / Match Odds / The Draw
            // Back	 	4.70	2.00	Lost	(2.00)	 	2,123.59
            -price.amount
        } else {
            // DRAW (HAN/GUA) 11/06/16 9:44 Lay 52721296 Standard 3.2 11/06/16 11:53 2 2
            // Fixtures 13 June Klubi-04 v FC Kiffen / Match Odds / The Draw
            // Lay	 	3.70	2.00	Won	 	2.00	2,125.59
            price.amount
        }
    }

    private fun getWinnersPowerSet(winnerCount: Int, selections: Set<Long>): List<Set<Long>> {
        return when (winnerCount) {
            0 -> selections.map { setOf(it) }.toList()
            else -> Sets.powerSet(selections).filter { it.size == winnerCount }.toList()
        }
    }
}

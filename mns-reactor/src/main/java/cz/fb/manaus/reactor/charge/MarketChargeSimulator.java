package cz.fb.manaus.reactor.charge;


import com.google.common.collect.ListMultimap;
import com.google.common.collect.Sets;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MarketChargeSimulator {


    public double getChargeMean(int winnerCount, double chargeRate, Map<Long, Double> probabilities, ListMultimap<Long, Price> bets) {
        var selections = probabilities.keySet();
        var winnerPowerSet = getWinnersPowerSet(winnerCount, selections);
        var chargeMean = 0d;
        for (var winners : winnerPowerSet) {
            var probability = getProbability(winners, probabilities);
            var profit = 0d;
            for (var selection : selections) {
                var isWinner = winners.contains(selection);
                var selectionBets = bets.get(selection);
                profit += selectionBets.stream()
                        .mapToDouble(isWinner ? this::profitWinner : this::profitLoser)
                        .sum();
            }
            if (profit > 0) {
                var charge = profit * chargeRate;
                chargeMean += probability * charge;

            }
        }
        return chargeMean;
    }

    private double getProbability(Set<Long> winners, Map<Long, Double> probabilities) {
        return winners.stream().mapToDouble(probabilities::get).reduce((left, right) -> left * right).getAsDouble();
    }


    private double profitWinner(Price price) {
        var result = (price.getPrice() - 1) * price.getAmount();
        if (price.getSide() == Side.BACK) {
            // DRAW (ENG/RUS) 03/06/16 11:08 Back 52500304 Standard 3.66 11/06/16 22:55 2 5.32
            // Fixtures 13 June Nadezhda Mogilev (W) v Zorka-BDU Minsk (W) / Match Odds / The Draw
            // Back	 	5.70	0.69	Won	 	3.24	2,133.79
            return result;
        } else {
            // DRAW (GAI/SIR) 11/06/16 8:52 Lay 52720548 Standard 3.62 11/06/16 17:59 2 -5.24
            // Fixtures 13 June Nadezhda Mogilev (W) v Zorka-BDU Minsk (W) / Match Odds / The Draw
            // Lay	 	5.10	2.00	Lost	(8.20)	 	2,125.59
            return -result;
        }
    }

    private double profitLoser(Price price) {
        if (price.getSide() == Side.BACK) {
            // DRAW (INT/AME) 10/06/16 23:32 Back 52710984 Standard 4.75 11/06/16 23:23 2 -2
            // Fixtures 13 June Madura Utd v Persiba Balikpapan / Match Odds / The Draw
            // Back	 	4.70	2.00	Lost	(2.00)	 	2,123.59
            return -price.getAmount();
        } else {
            // DRAW (HAN/GUA) 11/06/16 9:44 Lay 52721296 Standard 3.2 11/06/16 11:53 2 2
            // Fixtures 13 June Klubi-04 v FC Kiffen / Match Odds / The Draw
            // Lay	 	3.70	2.00	Won	 	2.00	2,125.59
            return price.getAmount();
        }
    }

    private List<Set<Long>> getWinnersPowerSet(int winnerCount, Set<Long> selections) {
        if (winnerCount == 0) {
            return selections.stream().map(Set::of).collect(Collectors.toList());
        }
        return Sets.powerSet(selections).stream().filter(w -> w.size() == winnerCount).collect(Collectors.toList());
    }

}

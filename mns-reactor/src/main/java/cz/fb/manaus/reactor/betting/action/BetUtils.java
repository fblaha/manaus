package cz.fb.manaus.reactor.betting.action;

import com.google.common.base.Splitter;
import com.google.common.collect.Ordering;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.BetActionType;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Component
public class BetUtils {

    public List<BetAction> getCurrentActions(List<BetAction> bets) {
        LinkedList<BetAction> lastUpdates = new LinkedList<>();
        if (bets != null) {
            BetAction first = bets.get(0);
            for (BetAction bet : bets) {
                validate(first, bet);
                if (bet.getBetActionType() != BetActionType.UPDATE) lastUpdates.clear();
                lastUpdates.addLast(bet);
            }
        }
        checkState(Ordering.from(comparing(BetAction::getActionDate)).isStrictlyOrdered(lastUpdates));
        return lastUpdates;
    }

    private void validate(BetAction first, BetAction bet) {
        checkArgument(first.getPrice().getSide() == bet.getPrice().getSide());
        checkArgument(first.getSelectionId() == bet.getSelectionId());
    }

    public List<Bet> getUnknownBets(List<Bet> bets, Set<String> myBets) {
        return bets.stream().filter(bet -> !myBets.contains(bet.getBetId())).collect(toList());
    }

    public List<String> parseProposers(String proposers) {
        return Splitter.on(',').omitEmptyStrings().trimResults().splitToList(proposers);
    }

    public SettledBet limitBetAmount(double ceiling, SettledBet bet) {
        Optional<Price> newPrice = limitPrice(ceiling, bet.getPrice());
        if (newPrice.isPresent()) {
            SettledBet copy = new SettledBet();
            BeanUtils.copyProperties(bet, copy);

            double amount = bet.getPrice().getAmount();
            double rate = ceiling / amount;
            copy.setProfitAndLoss(rate * bet.getProfitAndLoss());
            limitActionAmount(ceiling, copy);

            newPrice.ifPresent(copy::setPrice);
            return copy;
        }
        limitActionAmount(ceiling, bet);
        return bet;
    }

    private Optional<Price> limitPrice(double ceiling, Price origPrice) {
        double amount = origPrice.getAmount();
        if (ceiling < amount) {
            Price newPrice = new Price();
            BeanUtils.copyProperties(origPrice, newPrice);
            newPrice.setAmount(ceiling);
            return Optional.of(newPrice);
        }
        return Optional.empty();
    }

    public void limitActionAmount(double ceiling, SettledBet betCopy) {
        BetAction orig = betCopy.getBetAction();
        if (orig != null) {
            BetAction actionCopy = new BetAction();
            BeanUtils.copyProperties(orig, actionCopy);
            Optional<Price> newPrice = limitPrice(ceiling, orig.getPrice());
            newPrice.ifPresent(actionCopy::setPrice);
            betCopy.setBetAction(actionCopy);
        }
    }
}

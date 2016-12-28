package cz.fb.manaus.reactor.betting.action;

import com.google.common.base.Splitter;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.BetActionType;
import cz.fb.manaus.core.model.SettledBet;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Iterables.getFirst;
import static java.util.Comparator.comparing;

@Component
public class BetUtils {

    public List<BetAction> getCurrentActions(List<BetAction> bets) {
        LinkedList<BetAction> lastUpdates = new LinkedList<>();
        if (bets != null) {
            BetAction first = getFirst(bets, null);
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
        return from(bets).filter(bet -> !myBets.contains(bet.getBetId())).toList();
    }

    public List<String> parseProposers(String proposers) {
        return Splitter.on(',').omitEmptyStrings().trimResults().splitToList(proposers);
    }

    public Optional<BetAction> findBestMatchingAction(SettledBet settledBet, double priceEps, List<BetAction> actions) {
        double price = settledBet.getPrice().getPrice();
        Instant placed = settledBet.getPlaced().toInstant();

        Stream<BetAction> current = actions.stream()
                .filter(a -> a.getSelectionId() == settledBet.getSelectionId())
                .filter(a -> Precision.equals(a.getPrice().getPrice(), price, priceEps));

        return current.min(Comparator.comparing(a -> Duration.between(a.getActionDate().toInstant(), placed).abs()));
    }

    public List<Bet> filterDuplicates(List<Bet> bets) {
        List<Bet> result = new LinkedList<>();
        for (Collection<Bet> theSameBets : Multimaps.index(bets, Bet::getBetId).asMap().values()) {
            result.add(theSameBets.stream().max(Comparator.comparingDouble(Bet::getMatchedAmount)).get());
        }
        return result;
    }



}
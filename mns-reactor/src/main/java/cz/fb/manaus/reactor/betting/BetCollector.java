package cz.fb.manaus.reactor.betting;

import com.google.common.collect.ImmutableList;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.Side;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Stream.concat;

@Deprecated
public class BetCollector {

    private final LinkedList<BetCommand> toUpdate = new LinkedList<>();
    private final LinkedList<BetCommand> toPlace = new LinkedList<>();
    private final LinkedList<Bet> toCancel = new LinkedList<>();

    public void updateBet(BetCommand command) {
        requireNonNull(command.getNewBet().getBetId());
        toUpdate.addLast(command);
    }

    public void placeBet(BetCommand betCommand) {
        checkState(betCommand.getNewBet().getBetId() == null);
        toPlace.addLast(betCommand);
    }

    public void cancelBet(Bet oldBet) {
        requireNonNull(oldBet.getBetId());
        toCancel.addLast(oldBet);
    }


    public List<BetCommand> getToPlace() {
        return ImmutableList.copyOf(toPlace);
    }

    public List<BetCommand> getToUpdate() {
        return ImmutableList.copyOf(toUpdate);
    }

    public List<Bet> getToCancel() {
        return ImmutableList.copyOf(toCancel);
    }

    public Optional<Bet> findBet(String marketId, int selId, final Side side) {
        Stream<Bet> placeOrUpdate = concat(toPlace.stream(), toUpdate.stream())
                .map(BetCommand::getNewBet);
        return concat(placeOrUpdate, toCancel.stream())
                .filter(bet -> bet.getMarketId().equals(marketId)
                        && bet.getSelectionId() == selId
                        && bet.getRequestedPrice().getSide() == side)
                .findAny();
    }

    public boolean isEmpty() {
        return toPlace.isEmpty() && toUpdate.isEmpty() && toCancel.isEmpty();
    }
}

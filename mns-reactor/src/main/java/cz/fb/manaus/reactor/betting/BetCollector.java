package cz.fb.manaus.reactor.betting;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.Side;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Lists.transform;

public class BetCollector {

    private final LinkedList<BetCommand> toUpdate = new LinkedList<>();
    private final LinkedList<BetCommand> toPlace = new LinkedList<>();
    private final LinkedList<Bet> toCancel = new LinkedList<>();

    public void updateBet(BetCommand command) {
        checkNotNull(command.getNewBet().getBetId());
        toUpdate.addLast(command);
    }

    public void placeBet(BetCommand betCommand) {
        checkState(betCommand.getNewBet().getBetId() == null);
        toPlace.addLast(betCommand);
    }

    public void cancelBet(Bet oldBet) {
        checkNotNull(oldBet.getBetId());
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

    public Bet findBet(String marketId, int selId, final Side side) {
        Iterable<Bet> bets = concat(Iterables.transform(concat(toUpdate, toPlace), BetCommand::getNewBet), toCancel);
        return from(bets).firstMatch(
                bet -> bet.getMarketId().equals(marketId)
                        && bet.getSelectionId() == selId
                        && bet.getRequestedPrice().getSide() == side)
                .orNull();
    }

    void callPlaceHandlers(List<String> ids) {
        callHandlersInternal(ids, getToPlace());
    }

    void callUpdateHandlers(List<String> ids) {
        callHandlersInternal(ids, getToUpdate());
    }

    private void callHandlersInternal(List<String> ids, List<BetCommand> betCommands) {
        checkState(ids.size() == betCommands.size());
        List<Consumer<String>> handlers = transform(betCommands, BetCommand::getBetIdHandler);
        checkState(ids.size() == handlers.size());
        for (int i = 0; i < ids.size(); i++) {
            Consumer<String> handler = handlers.get(i);
            if (handler != null) {
                handler.accept(ids.get(i));
            }
        }
    }

    public boolean isEmpty() {
        return toPlace.isEmpty() && toUpdate.isEmpty() && toCancel.isEmpty();
    }
}

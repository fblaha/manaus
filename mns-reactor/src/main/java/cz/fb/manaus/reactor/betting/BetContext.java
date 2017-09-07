package cz.fb.manaus.reactor.betting;

import com.google.common.collect.Table;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.BetActionType;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.MarketSnapshot;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.model.TradedVolume;
import cz.fb.manaus.reactor.price.Fairness;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

public class BetContext {
    private final Side side;
    private final long selectionId;
    private final MarketSnapshot marketSnapshot;
    private final Set<String> tags = new HashSet<>();
    private final Map<String, String> properties = new HashMap<>();
    private final Fairness fairness;
    private final OptionalDouble chargeGrowthForecast;
    private Optional<Price> newPrice = Optional.empty();

    BetContext(Side side, long selectionId, OptionalDouble chargeGrowthForecast, MarketSnapshot marketSnapshot,
               Fairness fairness) {
        this.side = side;
        this.selectionId = selectionId;
        this.chargeGrowthForecast = chargeGrowthForecast;
        this.fairness = fairness;
        this.marketSnapshot = marketSnapshot;
    }

    public RunnerPrices getRunnerPrices() {
        return marketSnapshot.getMarketPrices().getRunnerPrices(selectionId);
    }

    public MarketPrices getMarketPrices() {
        return marketSnapshot.getMarketPrices();
    }

    public Optional<TradedVolume> getActualTradedVolume() {
        if (marketSnapshot.getTradedVolume().isPresent()) {
            return Optional.of(marketSnapshot.getTradedVolume().get().get(selectionId));
        } else {
            return Optional.empty();
        }
    }

    public Set<String> getTags() {
        return tags;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public Fairness getFairness() {
        return fairness;
    }

    public Optional<Bet> getOldBet() {
        return ofNullable(marketSnapshot.getCoverage().get(side, selectionId));
    }

    public Optional<Bet> getCounterBet() {
        return ofNullable(marketSnapshot.getCoverage().get(side.getOpposite(), selectionId));
    }

    public Optional<Price> getNewPrice() {
        return newPrice;
    }

    public Side getSide() {
        return side;
    }

    public Table<Side, Long, Bet> getCoverage() {
        return marketSnapshot.getCoverage();
    }

    public long getSelectionId() {
        return selectionId;
    }

    public OptionalDouble getChargeGrowthForecast() {
        return chargeGrowthForecast;
    }

    public MarketSnapshot getMarketSnapshot() {
        return marketSnapshot;
    }

    public BetContext withNewPrice(Price newPrice) {
        Side newSide = requireNonNull(newPrice.getSide());
        checkState(side == newSide);
        Optional<Bet> oldBet = getOldBet();
        if (oldBet.isPresent()) {
            Side oldSide = requireNonNull(oldBet.get().getRequestedPrice().getSide());
            checkState(oldSide == newSide);
        }
        Optional<Bet> counterBet = getCounterBet();
        counterBet.ifPresent(bet -> {
            Side otherSide = requireNonNull(bet.getRequestedPrice().getSide());
            checkState(otherSide == newSide.getOpposite());
        });
        this.newPrice = Optional.of(newPrice);
        return this;
    }

    public BetAction createBetAction() {
        BetActionType type = getOldBet().isPresent() ? BetActionType.UPDATE : BetActionType.PLACE;
        MarketPrices marketPrices = marketSnapshot.getMarketPrices();
        BetAction action = new BetAction(type, new Date(), newPrice.get(), marketPrices.getMarket(), selectionId);
        action.setMarketPrices(marketPrices);
        action.setProperties(properties);
        action.setTags(tags);
        return action;
    }

    public SettledBet simulateSettledBet() {
        BetAction action = createBetAction();
        SettledBet bet = new SettledBet(action.getSelectionId(), null, 0d, null, action.getPrice());
        bet.setBetAction(action);
        bet.setPlaced(action.getActionDate());
        return bet;
    }

    public boolean isCounterHalfMatched() {
        Optional<Bet> counterBet = getCounterBet();
        return counterBet.map(Bet::isHalfMatched).orElse(false);
    }

    public boolean isUpdate() {
        return getOldBet().isPresent();
    }


}

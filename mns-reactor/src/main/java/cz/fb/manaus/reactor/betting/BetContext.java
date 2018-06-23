package cz.fb.manaus.reactor.betting;

import com.google.common.collect.Table;
import cz.fb.manaus.core.model.AccountMoney;
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
    private final Map<String, String> properties = new HashMap<>();
    private final Fairness fairness;
    private final OptionalDouble chargeGrowthForecast;
    private final Optional<AccountMoney> accountMoney;
    private final Set<String> categoryBlacklist;
    private Optional<Price> newPrice = Optional.empty();

    BetContext(Side side, long selectionId, Optional<AccountMoney> accountMoney, OptionalDouble chargeGrowthForecast, MarketSnapshot marketSnapshot,
               Fairness fairness, Set<String> categoryBlacklist) {
        this.side = side;
        this.selectionId = selectionId;
        this.accountMoney = accountMoney;
        this.chargeGrowthForecast = chargeGrowthForecast;
        this.fairness = fairness;
        this.marketSnapshot = marketSnapshot;
        this.categoryBlacklist = categoryBlacklist;
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
        var oldBet = getOldBet();
        if (oldBet.isPresent()) {
            var oldSide = requireNonNull(oldBet.get().getRequestedPrice().getSide());
            checkState(oldSide == newSide);
        }
        var counterBet = getCounterBet();
        counterBet.ifPresent(bet -> {
            Side otherSide = requireNonNull(bet.getRequestedPrice().getSide());
            checkState(otherSide == newSide.getOpposite());
        });
        this.newPrice = Optional.of(newPrice);
        return this;
    }

    public BetAction createBetAction() {
        var type = getOldBet().isPresent() ? BetActionType.UPDATE : BetActionType.PLACE;
        var marketPrices = marketSnapshot.getMarketPrices();
        var action = new BetAction();
        action.setBetActionType(type);
        action.setActionDate(new Date());
        action.setMarket(marketPrices.getMarket());
        action.setSelectionId(selectionId);
        action.setMarketPrices(marketPrices);
        action.setProperties(properties);
        action.setPrice(newPrice.get());
        return action;
    }

    public SettledBet simulateSettledBet() {
        var action = createBetAction();
        var bet = SettledBet.create(action.getSelectionId(), null, 0d, null, action.getPrice());
        bet.setBetAction(action);
        bet.setPlaced(action.getActionDate());
        return bet;
    }

    public boolean isCounterHalfMatched() {
        var counterBet = getCounterBet();
        return counterBet.map(Bet::isHalfMatched).orElse(false);
    }

    public Optional<AccountMoney> getAccountMoney() {
        return accountMoney;
    }

    public boolean isUpdate() {
        return getOldBet().isPresent();
    }


}

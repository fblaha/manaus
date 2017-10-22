package cz.fb.manaus.reactor.betting;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import cz.fb.manaus.core.manager.MarketFilterService;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.CollectedBets;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.MarketSnapshot;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.betting.action.ActionSaver;
import cz.fb.manaus.reactor.betting.action.BetUtils;
import cz.fb.manaus.reactor.betting.listener.MarketSnapshotListener;
import cz.fb.manaus.reactor.price.AbstractPriceFilter;
import cz.fb.manaus.spring.DatabaseComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Ordering.from;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

@Lazy
@DatabaseComponent
public class BetManager {

    public static final String DISABLED_LISTENERS_EL = "#{systemEnvironment['MNS_DISABLED_LISTENERS']}";
    private static final Logger log = Logger.getLogger(BetManager.class.getSimpleName());
    private final Set<String> disabledListeners;
    @Autowired
    private BetUtils betUtils;
    @Autowired
    private MarketFilterService filterService;
    @Autowired
    private Optional<AbstractPriceFilter> priceFilter;
    @Autowired
    private ActionSaver actionSaver;
    private List<MarketSnapshotListener> marketSnapshotListeners = new LinkedList<>();


    @Autowired
    public BetManager(@Value(DISABLED_LISTENERS_EL) String rawDisabledListeners) {
        this.disabledListeners = ImmutableSet.copyOf(Splitter.on(',')
                .omitEmptyStrings()
                .trimResults()
                .split(Strings.nullToEmpty(rawDisabledListeners)));
    }

    @Autowired(required = false)
    public void setMarketSnapshotListeners(List<MarketSnapshotListener> marketSnapshotListeners) {
        requireNonNull(marketSnapshotListeners);
        this.marketSnapshotListeners = from(new AnnotationAwareOrderComparator()).sortedCopy(marketSnapshotListeners);
    }

    public CollectedBets fire(MarketSnapshot snapshot, Set<String> myBets) {
        MarketPrices marketPrices = snapshot.getMarketPrices();
        filterPrices(marketPrices);

        OptionalDouble reciprocal = marketPrices.getReciprocal(Side.BACK);
        Market market = marketPrices.getMarket();
        BetCollector collector = new BetCollector();

        if (checkMarket(myBets, market, reciprocal)) {
            validateOpenDate(market);

            List<Bet> unknownBets = betUtils.getUnknownBets(snapshot.getCurrentBets(), myBets);
            unknownBets.forEach(bet -> log.log(Level.WARNING, "unknown bet ''{0}''", bet));
            if (unknownBets.isEmpty()) {
                for (MarketSnapshotListener listener : marketSnapshotListeners) {
                    if (!disabledListeners.contains(listener.getClass().getSimpleName())) {
                        listener.onMarketSnapshot(snapshot, collector);
                    }
                }
                saveActions(collector.getToPlace());
                saveActions(collector.getToUpdate());
            }
        }
        return collector.toCollectedBets();
    }

    public void validateOpenDate(Market market) {
        Date currDate = new Date();
        Date openDate = market.getEvent().getOpenDate();
        checkState(currDate.before(openDate),
                "current %s, open date %s", currDate, openDate);
    }

    private void saveActions(List<BetCommand> commands) {
        List<BetAction> actions = commands.stream().map(BetCommand::getAction).collect(toList());
        actions.forEach(actionSaver::saveAction);
        commands.forEach(c -> c.getBet().setActionId(c.getAction().getId()));
    }

    private void filterPrices(MarketPrices marketPrices) {
        if (priceFilter.isPresent()) {
            for (RunnerPrices runnerPrices : marketPrices.getRunnerPrices()) {
                List<Price> prices = runnerPrices.getPrices().stream().collect(toList());
                List<Price> filtered = priceFilter.get().filter(prices);
                runnerPrices.setPrices(filtered);
            }
        } else {
            log.log(Level.WARNING, "No price filtering configured.");
        }
    }

    private boolean checkMarket(Set<String> myBets, Market market, OptionalDouble reciprocal) {
        return reciprocal.isPresent() && checkFilter(myBets, market);
    }

    private boolean checkFilter(Set<String> myBets, Market market) {
        return !myBets.isEmpty() || filterService.accept(market);
    }


}
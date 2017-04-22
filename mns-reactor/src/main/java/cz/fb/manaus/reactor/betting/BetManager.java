package cz.fb.manaus.reactor.betting;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import cz.fb.manaus.core.dao.MarketPricesDao;
import cz.fb.manaus.core.manager.MarketFilterService;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.MarketSnapshot;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.betting.action.BetUtils;
import cz.fb.manaus.reactor.betting.listener.MarketSnapshotListener;
import cz.fb.manaus.reactor.price.AbstractPriceFilter;
import cz.fb.manaus.spring.DatabaseComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

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
import static java.util.stream.Collectors.toList;

@Lazy
@DatabaseComponent
public class BetManager {

    public static final String PREVIEW_MODE_EL = "#{systemEnvironment['MNS_PREVIEW_MODE'] ?: false}";
    public static final String DISABLED_LISTENERS_EL = "#{systemEnvironment['MNS_DISABLED_LISTENERS']}";
    private static final Logger log = Logger.getLogger(BetManager.class.getSimpleName());
    private final boolean previewMode;
    private final Set<String> disabledListeners;
    @Autowired
    private BetUtils betUtils;
    @Autowired
    private MarketFilterService filterService;
    @Autowired
    private Optional<AbstractPriceFilter> priceFilter;
    @Autowired
    private MarketPricesDao marketPricesDao;
    @Autowired
    private BetService betService;
    private List<MarketSnapshotListener> marketSnapshotListeners = new LinkedList<>();


    @Autowired
    public BetManager(@Value(PREVIEW_MODE_EL) boolean previewMode,
                      @Value(DISABLED_LISTENERS_EL) String rawDisabledListeners) {
        this.previewMode = previewMode;
        this.disabledListeners = ImmutableSet.copyOf(Splitter.on(',')
                .omitEmptyStrings()
                .trimResults()
                .split(Strings.nullToEmpty(rawDisabledListeners)));
    }

    @Autowired
    public void setMarketSnapshotListeners(List<MarketSnapshotListener> marketSnapshotListeners) {
        Preconditions.checkNotNull(marketSnapshotListeners);
        this.marketSnapshotListeners = from(new AnnotationAwareOrderComparator()).sortedCopy(marketSnapshotListeners);
    }

    public void silentFire(MarketSnapshot snapshot, Set<String> myBets) {
        try {
            fire(snapshot, myBets);
        } catch (HttpStatusCodeException e) {
            String body = e.getResponseBodyAsString();
            HttpStatus statusCode = e.getStatusCode();
            String statusText = e.getStatusText();
            log.log(Level.SEVERE, "Http error, status: ''{0}'', status text: ''{1}'', body: ''{2}''",
                    new Object[]{statusCode, statusText, body});
            logException(snapshot, e);
        } catch (RuntimeException e) {
            logException(snapshot, e);
        }
    }

    private void logException(MarketSnapshot snapshot, RuntimeException e) {
        log.log(Level.SEVERE, "Error emerged for ''{0}''", snapshot);
        log.log(Level.SEVERE, "fix it!", e);
    }

    private void fire(MarketSnapshot snapshot, Set<String> myBets) {
        MarketPrices marketPrices = snapshot.getMarketPrices();
        filterPrices(marketPrices);

        OptionalDouble reciprocal = marketPrices.getReciprocal(Side.BACK);
        Market market = marketPrices.getMarket();

        if (checkMarket(myBets, market, reciprocal)) {
            Date currDate = new Date();
            checkState(currDate.before(market.getEvent().getOpenDate()));

            List<Bet> unknownBets = betUtils.getUnknownBets(snapshot.getCurrentBets(), myBets);
            unknownBets.forEach(bet -> log.log(Level.WARNING, "unknown bet ''{0}''", bet));
            if (unknownBets.isEmpty()) {
                BetCollector collector = new BetCollector();
                for (MarketSnapshotListener listener : marketSnapshotListeners) {
                    if (!disabledListeners.contains(listener.getClass().getSimpleName())) {
                        listener.onMarketSnapshot(snapshot, collector);
                    }
                }

                List<Bet> toCancel = collector.getToCancel();
                if (!toCancel.isEmpty()) {
                    betService.cancelBets(toCancel);
                }

                if (!collector.isEmpty()) {
                    Preconditions.checkState(marketPrices.getId() == null);
                    marketPricesDao.saveOrUpdate(marketPrices);
                    Preconditions.checkNotNull(marketPrices.getId());
                }

                List<BetCommand> toPlace = collector.getToPlace();
                if (!previewMode) {
                    if (!toPlace.isEmpty()) {
                        List<Bet> bets = toPlace.stream().map(BetCommand::getNewBet).collect(toList());
                        List<String> ids = betService.placeBets(bets);
                        collector.callPlaceHandlers(ids);
                    }
                    List<BetCommand> toUpdate = collector.getToUpdate();
                    if (!toUpdate.isEmpty()) {
                        List<Bet> bets = toUpdate.stream().map(BetCommand::getNewBet).collect(toList());
                        List<String> ids = betService.updateBets(bets);
                        collector.callUpdateHandlers(ids);
                    }
                }
            }
        }
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
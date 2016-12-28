package cz.fb.manaus.reactor.categorizer;

import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.SettledBet;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Range.closedOpen;
import static com.google.common.collect.Range.downTo;
import static com.google.common.collect.Range.upTo;
import static java.time.Duration.between;

public abstract class AbstractBeforeCategorizer implements SettledBetCategorizer {
    public static final String NEGATIVE = "<0";
    public static final String DAY = "_day_";
    public static final String HOUR = "_hour_";
    public static final String MIN = "_min_";
    private static final Logger log = Logger.getLogger(AbstractBeforeCategorizer.class.getSimpleName());
    private final String category;

    protected AbstractBeforeCategorizer(String category) {
        this.category = category;
    }

    protected abstract Date getDate(SettledBet settledBet);

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        Date date = getDate(settledBet);
        if (date == null) return Collections.emptySet();
        Market market = settledBet.getBetAction().getMarket();
        if (date.after(market.getEvent().getOpenDate())) {
            log.log(Level.WARNING, "BEFORE_RESOLVER: ''{0}'' date ''{1}'' after market start  ''{2}''", new Object[]{category, date, market});
        }
        long diffDay = between(date.toInstant(), market.getEvent().getOpenDate().toInstant()).toDays();
        Set<String> result = new HashSet<>();
        result.add(checkNotNull(getDayMap().get(diffDay)));
        if (diffDay == 0) {
            handleDay(date, market, result);
        }
        return result;
    }

    private void handleDay(Date date, Market market, Set<String> result) {
        long diffHours = between(date.toInstant(), market.getEvent().getOpenDate().toInstant()).toHours();
        result.add(checkNotNull(getHourMap().get(diffHours)));
        if (diffHours == 0) {
            handleMin(date, market, result);

        }
    }

    private void handleMin(Date date, Market market, Set<String> result) {
        long diffMin = between(date.toInstant(), market.getEvent().getOpenDate().toInstant()).toMinutes();
        result.add(checkNotNull(getMinMap().get(diffMin)));
    }

    protected RangeMap<Long, String> getDayMap() {
        return ImmutableRangeMap.<Long, String>builder()
                .put(Range.upTo(0l, BoundType.OPEN), category + DAY + NEGATIVE)
                .put(Range.singleton(0l), category + DAY + "0-1")
                .put(Range.singleton(1l), category + DAY + "1-2")
                .put(Range.singleton(2l), category + DAY + "2-3")
                .put(closedOpen(3l, 6l), category + DAY + "3-6")
                .put(downTo(6l, BoundType.CLOSED), category + DAY + "6d+")
                .build();
    }

    protected RangeMap<Long, String> getHourMap() {
        return ImmutableRangeMap.<Long, String>builder()
                .put(Range.upTo(0l, BoundType.OPEN), category + HOUR + NEGATIVE)
                .put(Range.singleton(0l), category + HOUR + "0-1")
                .put(Range.singleton(1l), category + HOUR + "1-2")
                .put(Range.singleton(2l), category + HOUR + "2-3")
                .put(closedOpen(3l, 6l), category + HOUR + "3-6")
                .put(closedOpen(6l, 12l), category + HOUR + "6-12")
                .put(downTo(12l, BoundType.CLOSED), category + HOUR + "12-24")
                .build();
    }

    protected RangeMap<Long, String> getMinMap() {
        return ImmutableRangeMap.<Long, String>builder()
                .put(upTo(0l, BoundType.OPEN), category + MIN + NEGATIVE)
                .put(closedOpen(0l, 10l), category + MIN + "0-10")
                .put(closedOpen(10l, 20l), category + MIN + "10-20")
                .put(closedOpen(20l, 30l), category + MIN + "20-30")
                .put(closedOpen(30l, 40l), category + MIN + "30-40")
                .put(closedOpen(40l, 50l), category + MIN + "40-50")
                .put(downTo(50l, BoundType.CLOSED), category + MIN + "50-60")
                .build();
    }

}

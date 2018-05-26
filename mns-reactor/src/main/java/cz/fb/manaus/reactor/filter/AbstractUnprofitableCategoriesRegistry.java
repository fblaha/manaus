package cz.fb.manaus.reactor.filter;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import cz.fb.manaus.core.MarketCategories;
import cz.fb.manaus.core.dao.BetActionDao;
import cz.fb.manaus.core.dao.SettledBetDao;
import cz.fb.manaus.core.maintanance.ConfigUpdate;
import cz.fb.manaus.core.model.ProfitRecord;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.reactor.profit.ProfitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.time.DateUtils.addDays;


abstract public class AbstractUnprofitableCategoriesRegistry {

    private static final String UNPROFITABLE_BLACK_LIST = "unprofitable.black.list.";
    private static final Logger log = Logger.getLogger(AbstractUnprofitableCategoriesRegistry.class.getSimpleName());
    private final String name;
    private final Duration period;
    private final double maximalProfit;
    private final Optional<Side> side;
    private final String filterPrefix;
    private final Map<Integer, Integer> thresholds;
    @Autowired
    private ProfitService profitService;
    @Autowired
    private SettledBetDao settledBetDao;
    @Autowired
    private BetActionDao betActionDao;
    @Autowired
    private ExchangeProvider provider;

    private List<String> whitelist;

    protected AbstractUnprofitableCategoriesRegistry(String name, Duration period,
                                                     Optional<Side> side,
                                                     double maximalProfit,
                                                     String filterPrefix,
                                                     Map<Integer, Integer> thresholds) {
        this.name = name;
        this.period = period;
        this.maximalProfit = maximalProfit;
        this.thresholds = thresholds;
        this.side = side;
        this.filterPrefix = filterPrefix;
    }

    @Autowired
    public void setWhitelist(@Value("#{systemEnvironment['MNS_CATEGORY_WHITE_LIST']}") String rawWhiteList) {
        this.whitelist = Splitter.on(',')
                .omitEmptyStrings()
                .trimResults()
                .splitToList(Strings.nullToEmpty(rawWhiteList));
    }

    private String getLogPrefix() {
        return String.format("UNPROFITABLE_REGISTRY(%s): ", name);
    }

    public void updateBlacklists(ConfigUpdate configUpdate) {
        log.log(Level.INFO, getLogPrefix() + "black list update started");
        var now = new Date();
        var settledBets = settledBetDao.getSettledBets(
                Optional.of(addDays(now, -(int) period.toDays())), Optional.of(now), side,
                OptionalInt.empty());
        betActionDao.fetchMarketPrices(settledBets.stream().map(SettledBet::getBetAction));
        if (settledBets.isEmpty()) return;
        var chargeRate = provider.getChargeRate();
        var profitRecords = profitService.getProfitRecords(settledBets, Optional.empty(), true, chargeRate);

        log.log(Level.INFO, getLogPrefix() + "updating registry ''{0}''", name);
        updateBlacklists(profitRecords, configUpdate);
    }

    void updateBlacklists(List<ProfitRecord> profitRecords, ConfigUpdate configUpdate) {
        var all = profitRecords.stream().filter(ProfitRecord::isAllCategory).findAny()
                .orElseThrow(() -> new IllegalStateException("missing: " + MarketCategories.ALL));
        var filtered = getFiltered(profitRecords);
        var totalCount = all.getTotalCount();

        configUpdate.getDeletePrefixes().add(getPropertyPrefix());

        var totalBlacklist = new HashSet<String>();
        for (var entry : thresholds.entrySet()) {
            int thresholdPct = entry.getKey();
            var threshold = getThreshold(thresholdPct);
            int blackCount = entry.getValue();
            var blacklist = getBlacklist(threshold, blackCount, totalCount, filtered.stream(), totalBlacklist);
            totalBlacklist.addAll(blacklist);
            saveBlacklist(thresholdPct, blacklist, configUpdate);
        }
    }

    private List<ProfitRecord> getFiltered(List<ProfitRecord> profitRecords) {
        if (Strings.isNullOrEmpty(filterPrefix)) {
            return profitRecords;
        } else {
            return profitRecords.stream()
                    .filter(r -> r.getCategory().startsWith(filterPrefix))
                    .collect(toList());
        }
    }

    void saveBlacklist(int thresholdPct, Set<String> blacklist, ConfigUpdate configUpdate) {
        if (!blacklist.isEmpty()) {
            configUpdate.getSetProperties().put(getPropertyPrefix() + thresholdPct, Joiner.on(',').join(new TreeSet<>(blacklist)));
        }
    }

    double getThreshold(int thresholdPct) {
        return thresholdPct / 100d;
    }

    Set<String> getBlacklist(double threshold, int blackCount, int totalCount, Stream<ProfitRecord> profitRecords,
                             Set<String> blacklist) {
        var currentBlacklist = new LinkedHashSet<String>();

        var sorted = profitRecords.filter(record -> (double) record.getTotalCount() / totalCount <= threshold)
                .sorted(comparingDouble(ProfitRecord::getProfit)).collect(toList());

        var i = 0;
        for (var weak : sorted) {
            if (i >= blackCount || weak.getProfit() >= maximalProfit) break;
            if (blacklist.contains(weak.getCategory())) continue;
            if (whitelist.stream().anyMatch(prefix -> weak.getCategory().startsWith(prefix))) continue;
            currentBlacklist.add(weak.getCategory());
            i++;
        }
        return currentBlacklist;
    }

    private String getPropertyPrefix() {
        return UNPROFITABLE_BLACK_LIST + name + ".";
    }

}

package cz.fb.manaus.reactor.filter;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import cz.fb.manaus.core.MarketCategories;
import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.CategoryService;
import cz.fb.manaus.core.dao.BetActionDao;
import cz.fb.manaus.core.dao.SettledBetDao;
import cz.fb.manaus.core.maintanance.ConfigUpdate;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.ProfitRecord;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.core.service.PropertiesService;
import cz.fb.manaus.reactor.profit.ProfitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.time.Duration;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.google.common.base.Splitter.on;
import static com.google.common.base.Suppliers.memoizeWithExpiration;
import static com.google.common.collect.Sets.intersection;
import static java.util.Comparator.comparingDouble;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.time.DateUtils.addDays;


abstract public class AbstractUnprofitableCategoriesRegistry {

    private static final String UNPROFITABLE_BLACK_LIST = "unprofitable.black.list.";
    private static final Logger log = Logger.getLogger(AbstractUnprofitableCategoriesRegistry.class.getSimpleName());
    private final String name;
    private final Duration period;
    private final double maximalProfit;
    private final Optional<Side> side;
    private final Duration pauseDuration;
    private final String filterPrefix;
    private final Pattern blackListProperty;
    private final Map<Integer, Integer> thresholds;
    @Autowired
    private PropertiesService propertiesService;
    private final Supplier<Set<String>> blackListSupplier = memoizeWithExpiration(
            this::getSavedBlackList, 5, TimeUnit.MINUTES);
    @Autowired
    private ProfitService profitService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SettledBetDao settledBetDao;
    @Autowired
    private BetActionDao betActionDao;
    @Autowired
    private ExchangeProvider provider;
    @Autowired
    private ApplicationContext context;

    private List<String> whiteList;

    protected AbstractUnprofitableCategoriesRegistry(String name, Duration period, Optional<Side> side, double maximalProfit,
                                                     Duration pauseDuration, String filterPrefix,
                                                     Map<Integer, Integer> thresholds) {
        this.name = name;
        this.period = period;
        this.maximalProfit = maximalProfit;
        this.thresholds = thresholds;
        this.side = side;
        this.pauseDuration = pauseDuration;
        this.filterPrefix = filterPrefix;
        this.blackListProperty = Pattern.compile("^" + Pattern.quote(getPropertyPrefix()) + "\\d{1,2}$");
    }

    @Autowired
    public void setWhiteList(@Value("#{systemEnvironment['MNS_CATEGORY_WHITE_LIST']}") String rawWhiteList) {
        this.whiteList = Splitter.on(',')
                .omitEmptyStrings()
                .trimResults()
                .splitToList(Strings.nullToEmpty(rawWhiteList));
    }

    public boolean checkMarket(Market market) {
        Set<String> categories = categoryService.getMarketCategories(market, false);
        Set<String> unprofitableCategories = getUnprofitableCategories(categories);
        if (!unprofitableCategories.isEmpty()) {
            log.log(Level.INFO, getLogPrefix() + "blacklist category ''{0}'' for market ''{1}''",
                    new Object[]{unprofitableCategories, market});
        }
        return unprofitableCategories.isEmpty();
    }

    public boolean checkBet(SettledBet settledBet) {
        Set<String> categories = categoryService.getSettledBetCategories(settledBet, true, BetCoverage.EMPTY);
        Set<String> unprofitableCategories = getUnprofitableCategories(categories);
        if (!unprofitableCategories.isEmpty()) {
            log.log(Level.INFO, getLogPrefix() + "blacklist category ''{0}'' for bet ''{1}''", new Object[]{unprofitableCategories, settledBet});
        }
        return unprofitableCategories.isEmpty();
    }


    public Set<String> getUnprofitableCategories(Set<String> categories) {
        Set<String> blackList = blackListSupplier.get();
        return intersection(categories, blackList);
    }

    Set<String> getSavedBlackList() {
        Set<String> blackList = new HashSet<>();
        for (Map.Entry<String, String> item : propertiesService.list(Optional.of(getPropertyPrefix())).entrySet()) {
            if (blackListProperty.matcher(item.getKey()).matches()) {
                String rawCategories = item.getValue();
                blackList.addAll(on(',').splitToList(rawCategories));
            }
        }
        log.log(Level.INFO, getLogPrefix() + "blacklist fetched ''{0}''", blackList);
        return blackList;
    }

    private String getLogPrefix() {
        return String.format("UNPROFITABLE_REGISTRY(%s): ", name);
    }

    public void updateBlackLists(ConfigUpdate configUpdate) {
        log.log(Level.INFO, getLogPrefix() + "black list update started");
        Date now = new Date();
        List<SettledBet> settledBets = settledBetDao.getSettledBets(
                of(addDays(now, -(int) period.toDays())), Optional.of(now), side,
                OptionalInt.empty());
        betActionDao.fetchMarketPrices(settledBets.stream().map(SettledBet::getBetAction));
        if (settledBets.isEmpty()) return;
        double chargeRate = provider.getChargeRate();
        List<ProfitRecord> profitRecords = profitService.getProfitRecords(settledBets, Optional.empty(), true, chargeRate);

        log.log(Level.INFO, getLogPrefix() + "updating registry ''{0}''", name);
        updateBlackLists(profitRecords, configUpdate);
    }

    void updateBlackLists(List<ProfitRecord> profitRecords, ConfigUpdate configUpdate) {
        ProfitRecord all = profitRecords.stream().filter(ProfitRecord::isAllCategory).findAny()
                .orElseThrow(() -> new IllegalStateException("missing: " + MarketCategories.ALL));
        List<ProfitRecord> filtered = getFiltered(profitRecords);
        int totalCount = all.getTotalCount();

        configUpdate.getDeletePrefixes().add(getPropertyPrefix());

        Set<String> totalBlackList = new HashSet<>();
        for (Map.Entry<Integer, Integer> entry : thresholds.entrySet()) {
            int thresholdPct = entry.getKey();
            double threshold = getThreshold(thresholdPct);
            int blackCount = entry.getValue();
            Set<String> blackList = getBlackList(threshold, blackCount, totalCount, filtered.stream(), totalBlackList);
            totalBlackList.addAll(blackList);
            saveBlackList(thresholdPct, blackList, configUpdate);
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

    void saveBlackList(int thresholdPct, Set<String> blackList, ConfigUpdate configUpdate) {
        if (!blackList.isEmpty()) {
            configUpdate.getSetProperties().put(getPropertyPrefix() + thresholdPct, Joiner.on(',').join(blackList));
        }
    }

    double getThreshold(int thresholdPct) {
        return thresholdPct / 100d;
    }

    Set<String> getBlackList(double threshold, int blackCount, int totalCount, Stream<ProfitRecord> profitRecords, Set<String> blackList) {
        Set<String> currentBlackList = new LinkedHashSet<>();

        List<ProfitRecord> sorted = profitRecords.filter(record -> (double) record.getTotalCount() / totalCount <= threshold)
                .sorted(comparingDouble(ProfitRecord::getProfit)).collect(toList());

        int i = 0;
        for (ProfitRecord weak : sorted) {
            if (i >= blackCount || weak.getProfit() >= maximalProfit) break;
            if (blackList.contains(weak.getCategory())) continue;
            if (whiteList.stream().anyMatch(prefix -> weak.getCategory().startsWith(prefix))) continue;
            currentBlackList.add(weak.getCategory());
            i++;
        }
        return currentBlackList;
    }

    private String getPropertyPrefix() {
        return UNPROFITABLE_BLACK_LIST + name + ".";
    }

}

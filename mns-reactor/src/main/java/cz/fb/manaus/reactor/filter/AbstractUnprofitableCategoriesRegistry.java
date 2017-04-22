package cz.fb.manaus.reactor.filter;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.CategoryService;
import cz.fb.manaus.core.category.categorizer.NamespaceAware;
import cz.fb.manaus.core.dao.BetActionDao;
import cz.fb.manaus.core.dao.SettledBetDao;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.ProfitRecord;
import cz.fb.manaus.core.model.Property;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.core.service.PeriodicTaskService;
import cz.fb.manaus.core.service.PropertiesService;
import cz.fb.manaus.reactor.profit.ProfitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Sets.intersection;
import static java.util.Comparator.comparingDouble;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.time.DateUtils.addDays;


abstract public class AbstractUnprofitableCategoriesRegistry implements NamespaceAware {

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
    private PeriodicTaskService periodicTaskService;
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
    private TransactionTemplate transactionTemplate;

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
        blackListProperty = Pattern.compile("^" + Pattern.quote(getPropertyPrefix()) + "\\d{1,2}$");
    }

    @Autowired
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Autowired
    public void setWhiteList(@Value("#{systemEnvironment['MNS_CATEGORY_WHITE_LIST']}") String rawWhiteList) {
        this.whiteList = Splitter.on(',')
                .omitEmptyStrings()
                .trimResults()
                .splitToList(Strings.nullToEmpty(rawWhiteList));
    }

    public boolean checkMarket(Market market) {
        Set<String> categories = categoryService.getMarketCategories(market, false, getNamespace());
        Set<String> unprofitableCategories = getUnprofitableCategories(categories);
        if (!unprofitableCategories.isEmpty()) {
            log.log(Level.INFO, getLogPrefix() + "blacklist category ''{0}'' for market ''{1}''",
                    new Object[]{unprofitableCategories, market});
        }
        return unprofitableCategories.isEmpty();
    }

    public boolean checkBet(SettledBet settledBet) {
        Set<String> categories = categoryService.getSettledBetCategories(settledBet, true, getNamespace(), BetCoverage.EMPTY);
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
        List<Property> items = propertiesService.list(Optional.of(getPropertyPrefix()));
        for (Property item : items) {
            if (!blackListProperty.matcher(item.getName()).matches()) continue;
            String rawCategories = item.getValue();
            blackList.addAll(on(',').splitToList(rawCategories));
        }
        log.log(Level.INFO, getLogPrefix() + "blacklist fetched ''{0}''", blackList);
        return blackList;
    }

    private String getLogPrefix() {
        return String.format("UNPROFITABLE_REGISTRY(%s): ", name);
    }

    public void updateBlackLists() {
        periodicTaskService.runIfExpired(getTaskName(), pauseDuration, this::doUpdate);
    }

    private String getTaskName() {
        return "unprofitable.registry.update." + name;
    }

    private void doUpdate() {
        log.log(Level.INFO, getLogPrefix() + "black list update started");
        Date now = new Date();
        List<SettledBet> settledBets = settledBetDao.getSettledBets(
                of(addDays(now, -(int) period.toDays())), Optional.of(now), side,
                OptionalInt.empty());
        betActionDao.fetchMarketPrices(settledBets.stream().map(SettledBet::getBetAction));
        if (settledBets.isEmpty()) return;
        double chargeRate = provider.getChargeRate();
        List<ProfitRecord> profitRecords = profitService.getProfitRecords(settledBets, Optional.empty(), true, getNamespace(), chargeRate);

        Collection<AbstractUnprofitableCategoriesRegistry> registries = context.getBeansOfType(AbstractUnprofitableCategoriesRegistry.class).values();
        registries.stream()
                .filter(registry -> registry.period == period &&
                        registry.side.equals(side) &&
                        Objects.equals(getNamespace(), registry.getNamespace()))
                .forEach(registry -> {
                    log.log(Level.INFO, getLogPrefix() + "updating registry ''{0}''", registry.name);
                    registry.updateBlackLists(profitRecords);
                });
    }

    void updateBlackLists(List<ProfitRecord> profitRecords) {
        ProfitRecord all = find(profitRecords, ProfitRecord::isAllCategory);
        List<ProfitRecord> filtered = getFiltered(profitRecords);
        final int totalCount = all.getTotalCount();
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                cleanUp();
                Set<String> totalBlackList = new HashSet<>();
                for (Map.Entry<Integer, Integer> entry : thresholds.entrySet()) {
                    int thresholdPct = entry.getKey();
                    double threshold = getThreshold(thresholdPct);
                    int blackCount = entry.getValue();
                    Set<String> blackList = getBlackList(threshold, blackCount, totalCount, filtered.stream(), totalBlackList);
                    totalBlackList.addAll(blackList);
                    saveBlackList(thresholdPct, blackList);
                }
                periodicTaskService.markUpdated(getTaskName());
            }
        });
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

    void cleanUp() {
        propertiesService.delete(getPropertyPrefix());
    }

    void saveBlackList(int thresholdPct, Set<String> blackList) {
        if (!blackList.isEmpty()) {
            propertiesService.set(getPropertyPrefix() + thresholdPct, Joiner.on(',').join(blackList), Duration.ofDays(7));
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

    String getPropertyPrefix() {
        return UNPROFITABLE_BLACK_LIST + name + ".";
    }

}

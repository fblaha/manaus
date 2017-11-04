package cz.fb.manaus.reactor.profit;

import com.google.common.base.Preconditions;
import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.CategoryService;
import cz.fb.manaus.core.model.ProfitRecord;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;


@Service
public class ProfitService {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProfitPlugin profitPlugin;

    public List<ProfitRecord> getProfitRecords(List<SettledBet> bets, Optional<String> projection,
                                               boolean simulationAwareOnly, Optional<String> namespace, double chargeRate) {
        BetCoverage coverage = BetCoverage.from(bets);
        Map<String, Double> charges = profitPlugin.getCharges(bets, chargeRate);

        if (projection.isPresent()) {
            bets = categoryService.filterBets(bets, projection.get(), requireNonNull(namespace), coverage);
        }

        List<ProfitRecord> betRecords = computeProfitRecords(bets, simulationAwareOnly,
                requireNonNull(namespace), charges, coverage);
        return mergeProfitRecords(betRecords);
    }

    public List<ProfitRecord> mergeProfitRecords(Collection<ProfitRecord> records) {
        Map<String, List<ProfitRecord>> categories = records.stream()
                .collect(Collectors.groupingBy(ProfitRecord::getCategory));
        return categories.entrySet().stream()
                .map(e -> mergeCategory(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(ProfitRecord::getCategory))
                .collect(toList());
    }

    public ProfitRecord mergeCategory(String category, Collection<ProfitRecord> records) {
        Preconditions.checkArgument(records.stream().map(ProfitRecord::getCategory)
                .allMatch(category::equals));
        double avgPrice = records.stream()
                .mapToDouble(ProfitRecord::getAvgPrice)
                .average().getAsDouble();
        double theoreticalProfit = records.stream().mapToDouble(ProfitRecord::getTheoreticalProfit).sum();
        double charge = records.stream().mapToDouble(ProfitRecord::getCharge).sum();
        int layCount = records.stream().mapToInt(ProfitRecord::getLayCount).sum();
        int backCount = records.stream().mapToInt(ProfitRecord::getBackCount).sum();
        int coverCount = records.stream().mapToInt(ProfitRecord::getCoverCount).sum();
        ProfitRecord result = new ProfitRecord(category, theoreticalProfit, layCount, backCount, avgPrice, charge);
        if (coverCount > 0) {
            OptionalDouble diff = records.stream().filter(profitRecord -> profitRecord.getCoverDiff() != null)
                    .mapToDouble(ProfitRecord::getCoverDiff).average();
            result.setCoverDiff(diff.getAsDouble());
            result.setCoverCount(coverCount);
        }
        return result;
    }

    private List<ProfitRecord> computeProfitRecords(List<SettledBet> bets, boolean simulationAwareOnly,
                                                    Optional<String> namespace, Map<String, Double> charges, BetCoverage coverage) {
        return bets.parallelStream().flatMap(bet -> {
            Set<String> categories = categoryService.getSettledBetCategories(bet, simulationAwareOnly, namespace, coverage);
            return categories.stream().map(category -> {
                double charge = charges.get(bet.getBetAction().getBetId());
                Preconditions.checkState(charge >= 0, charge);
                return toProfitRecord(bet, category, charge, coverage);
            });
        }).collect(Collectors.toList());
    }

    public ProfitRecord toProfitRecord(SettledBet bet, String category, double chargeContribution, BetCoverage coverage) {
        Side type = requireNonNull(bet.getPrice().getSide());
        double price = bet.getPrice().getPrice();
        ProfitRecord result;
        if (type == Side.BACK) {
            result = new ProfitRecord(category, bet.getProfitAndLoss(), 0, 1, price, chargeContribution);
        } else {
            result = new ProfitRecord(category, bet.getProfitAndLoss(), 1, 0, price, chargeContribution);
        }
        String marketId = bet.getBetAction().getMarket().getId();
        long selectionId = bet.getSelectionId();
        if (coverage.isCovered(marketId, selectionId)) {
            double backPrice = coverage.getPrice(marketId, selectionId, Side.BACK);
            double layPrice = coverage.getPrice(marketId, selectionId, Side.LAY);
            result.setCoverDiff(backPrice - layPrice);
            result.setCoverCount(1);
        }
        return result;
    }

}

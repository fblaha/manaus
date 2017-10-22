package cz.fb.manaus.reactor.betting.listener;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.collect.Table;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.MarketSnapshot;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Runner;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.model.TradedVolume;
import cz.fb.manaus.reactor.betting.BetCollector;
import cz.fb.manaus.reactor.betting.BetCommand;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.BetContextFactory;
import cz.fb.manaus.reactor.betting.proposer.PriceProposalService;
import cz.fb.manaus.reactor.betting.proposer.PriceProposer;
import cz.fb.manaus.reactor.betting.proposer.ProposedPrice;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import cz.fb.manaus.reactor.betting.validator.ValidationService;
import cz.fb.manaus.reactor.betting.validator.Validator;
import cz.fb.manaus.reactor.price.Fairness;
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator;
import cz.fb.manaus.reactor.price.PriceService;
import cz.fb.manaus.reactor.rounding.RoundingService;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Ordering.from;
import static cz.fb.manaus.reactor.betting.listener.ProbabilityComparator.COMPARATORS;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

public abstract class AbstractUpdatingBettor implements MarketSnapshotListener {

    private static final Logger log = Logger.getLogger(AbstractUpdatingBettor.class.getSimpleName());

    protected final Side side;
    protected final List<Validator> validators;
    protected final List<PriceProposer> proposers;
    @Autowired
    protected ValidationService validationService;
    @Autowired
    protected PriceService priceService;
    @Autowired
    private FlowFilterRegistry flowFilterRegistry;
    @Autowired
    private RoundingService roundingService;
    @Autowired
    private PriceProposalService proposalService;
    @Autowired
    private FairnessPolynomialCalculator calculator;
    @Autowired
    private BetContextFactory contextFactory;
    @Autowired
    private MetricRegistry metricRegistry;


    protected AbstractUpdatingBettor(Side side, List<Validator> validators,
                                     List<PriceProposer> proposers) {
        this.validators = validators;
        this.side = side;
        this.proposers = proposers;
    }

    protected List<PriceProposer> getSortedProposers(List<PriceProposer> proposers) {
        return from(new AnnotationAwareOrderComparator()).sortedCopy(proposers);
    }

    @PostConstruct
    public void logConfig() {
        String validatorList = this.validators.stream().map(Validator::getClass)
                .map(Class::getSimpleName).sorted().collect(Collectors.joining(","));
        String proposerList = this.proposers.stream().map(Validator::getClass)
                .map(Class::getSimpleName).sorted().collect(Collectors.joining(","));
        log.log(Level.INFO, "Bettor class: ''{0}'', validators: ''{1}'', proposers: ''{2}''",
                new Object[]{this.getClass().getSimpleName(), validatorList, proposerList});
    }

    @Override
    public final void onMarketSnapshot(MarketSnapshot snapshot, BetCollector betCollector) {
        MarketPrices marketPrices = snapshot.getMarketPrices();
        Market market = marketPrices.getMarket();
        int winnerCount = marketPrices.getWinnerCount();
        FlowFilter flowFilter = flowFilterRegistry.getFlowFilter(market.getType());
        if (flowFilter.getWinnerCountRange().contains(winnerCount)) {
            Table<Side, Long, Bet> coverage = snapshot.getCoverage();
            Fairness fairness = calculator.getFairness(marketPrices);
            Optional<Side> credibleSide = requireNonNull(fairness.getMoreCredibleSide());
            Ordering<RunnerPrices> ordering = COMPARATORS.get(credibleSide.get());
            ImmutableList<RunnerPrices> prices = ordering.immutableSortedCopy(marketPrices.getRunnerPrices());
            checkState(prices.stream()
                    .map(RunnerPrices::getSelectionId).distinct().count() == prices.stream()
                    .map(RunnerPrices::getSelectionId)
                    .count());

            for (int i = 0; i < prices.size(); i++) {
                RunnerPrices runnerPrices = prices.get(i);
                long selectionId = runnerPrices.getSelectionId();
                Runner runner = market.getRunner(selectionId);
                boolean activeSelection = coverage.contains(side, selectionId)
                        || coverage.contains(side.getOpposite(), selectionId);
                boolean accepted = flowFilter.getIndexRange().contains(i)
                        && flowFilter.getRunnerPredicate().test(market, runner);
                if (activeSelection || accepted) {
                    Optional<Bet> oldBet = ofNullable(coverage.get(side, selectionId));
                    BetContext ctx = contextFactory.create(side, selectionId,
                            snapshot, fairness);
                    setTradedVolumeMean(ctx);
                    ValidationResult pricelessValidation = validationService.validate(ctx, validators);
                    if (!pricelessValidation.isSuccess()) {
                        cancelBet(oldBet, betCollector);
                        continue;
                    }

                    Optional<Price> newPrice = getNewPrice(ctx);
                    if (!newPrice.isPresent()) {
                        cancelBet(oldBet, betCollector);
                        continue;
                    }

                    BetContext priceCtx = ctx.withNewPrice(newPrice.get());

                    if (oldBet.isPresent() && oldBet.get().isMatched()) continue;

                    ValidationResult priceValidation = validationService.validate(priceCtx, validators);

                    if (priceValidation.isSuccess()) {
                        bet(priceCtx, betCollector);
                    }
                }
            }
        }
    }

    private void bet(BetContext ctx, BetCollector betCollector) {
        BetAction action = ctx.createBetAction();
        Price newPrice = ctx.getNewPrice().get();

        if (ctx.getOldBet().isPresent()) {
            betCollector.updateBet(new BetCommand(ctx.getOldBet().get().replacePrice(newPrice.getPrice()), action));
        } else {
            Market market = ctx.getMarketPrices().getMarket();
            Bet bet = new Bet(null, market.getId(), ctx.getRunnerPrices().getSelectionId(), newPrice, null, 0);
            betCollector.placeBet(new BetCommand(bet, action));
        }
        log.log(Level.INFO, "{0}_BET:  new bet ''{1}''", new Object[]{action.getBetActionType(), action});
    }

    private void cancelBet(Optional<Bet> oldBet, BetCollector betCollector) {
        oldBet.ifPresent(bet -> {
            if (!bet.isMatched()) {
                metricRegistry.counter("bet.CANCEL").inc();
                betCollector.cancelBet(bet);
                log.log(Level.INFO, "CANCEL_BET: unable propose price for bet ''{0}''", bet);
            }
        });
    }

    protected OptionalDouble reducePrices(BetContext context) {
        ProposedPrice proposedPrice = proposalService.reducePrices(context, proposers, context.getSide());
        OptionalDouble rounded = roundingService.roundBet(proposedPrice.getPrice());
        if (rounded.isPresent()) {
            context.getProperties().put(BetAction.PROPOSER_PROP, Joiner.on(',').join(proposedPrice.getProposers()));
        }
        return rounded;
    }

    private void setTradedVolumeMean(BetContext context) {
        Optional<TradedVolume> tradedVolume = context.getActualTradedVolume();
        if (tradedVolume.isPresent()) {
            OptionalDouble weightedMean = tradedVolume.get().getWeightedMean();
            if (weightedMean.isPresent()) {
                setProperty(BetAction.TRADED_VOL_MEAN, weightedMean.getAsDouble(), context.getProperties());
            }
        }
    }

    private void setProperty(String key, Double value, Map<String, String> properties) {
        if (value != null) properties.put(key, Double.toString(Precision.round(value, 4)));
    }

    protected abstract Optional<Price> getNewPrice(BetContext betContext);

}

package cz.fb.manaus.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.FluentIterable.from;

@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
public class MarketPrices implements SideMixed<MarketPrices> {

    public static final double FAIR_EPS = 0.001d;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private int winnerCount;

    @Column(nullable = false)
    private Date time;
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Market market;
    @JoinColumn(name = "marketPrices_id")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    private Collection<RunnerPrices> runnerPrices;

    public MarketPrices() {
    }

    public MarketPrices(int winnerCount, Market market, Collection<RunnerPrices> runnerPrices, Date time) {
        this.winnerCount = winnerCount;
        this.market = market;
        this.runnerPrices = runnerPrices;
        this.time = time;
    }

    public MarketPrices(int winnerCount, Market market, Collection<RunnerPrices> runnerPrices) {
        this(winnerCount, market, runnerPrices, new Date());
    }

    public static double getOverround(List<Double> bestPrices) {
        return bestPrices.stream().mapToDouble(p -> 1 / p).sum();
    }

    @JsonIgnore
    public OptionalDouble getOverround(Side type) {
        FluentIterable<Optional<Price>> bestPrices = getBestPricesIterable(checkNotNull(type));
        if (bestPrices.allMatch(Optional::isPresent)) {
            Preconditions.checkState(bestPrices.allMatch(price -> price.get().getSide() == type));
            return OptionalDouble.of(getOverround(bestPrices
                    .transform(Optional::get)
                    .transform(Price::getPrice).toList()));
        }
        return OptionalDouble.empty();
    }

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getWinnerCount() {
        return winnerCount;
    }

    public RunnerPrices getRunnerPrices(long selectionId) {
        return runnerPrices.stream()
                .filter(rp -> rp.getSelectionId() == selectionId)
                .findAny().get();
    }

    @JsonIgnore
    public OptionalDouble getReciprocal(Side side) {
        OptionalDouble overround = getOverround(side);
        if (overround.isPresent()) {
            return OptionalDouble.of(winnerCount / overround.getAsDouble());
        } else {
            return OptionalDouble.empty();
        }
    }

    @JsonIgnore
    public OptionalDouble getLastMatchedReciprocal() {
        for (RunnerPrices runnerPrices : getRunnerPrices()) {
            if (runnerPrices.getLastMatchedPrice() == null) return OptionalDouble.empty();
        }
        ImmutableList<Double> prices = from(getRunnerPrices()).transform(RunnerPrices::getLastMatchedPrice).toList();
        return OptionalDouble.of(winnerCount / getOverround(prices));
    }

    public Integer getId() {
        return id;
    }

    public Collection<RunnerPrices> getRunnerPrices() {
        return runnerPrices;
    }

    @Override
    public MarketPrices getHomogeneous(Side side) {
        List<RunnerPrices> newList = new LinkedList<>();
        for (RunnerPrices runnerPrices : getRunnerPrices()) {
            newList.add(runnerPrices.getHomogeneous(side));
        }
        return new MarketPrices(winnerCount, market, newList, getTime());
    }

    public List<OptionalDouble> getBestPrices(Side type) {
        return getBestPricesIterable(checkNotNull(type))
                .transform(this::getOptionalPrice)
                .toList();
    }

    private OptionalDouble getOptionalPrice(Optional<Price> price) {
        if (price.isPresent()) {
            return OptionalDouble.of(price.get().getPrice());
        } else {
            return OptionalDouble.empty();
        }
    }

    private FluentIterable<Optional<Price>> getBestPricesIterable(Side type) {
        return from(getHomogeneous(type).getRunnerPrices())
                .transform(RunnerPrices::getBestPrice);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("winnerCount", winnerCount)
                .add("time", time)
                .add("market", market)
                .add("runnerPrices", runnerPrices)
                .toString();
    }
}

package cz.fb.manaus.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
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
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

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
    private Collection<RunnerPrices> runnerPrices;

    public MarketPrices() {
    }

    public MarketPrices(int winnerCount, Market market, Collection<RunnerPrices> runnerPrices, Date time) {
        this.winnerCount = winnerCount;
        this.market = market;
        this.runnerPrices = runnerPrices;
        this.time = time;
    }

    public static double getOverround(List<Double> bestPrices) {
        return bestPrices.stream().mapToDouble(p -> 1 / p).sum();
    }

    @JsonIgnore
    public OptionalDouble getOverround(Side type) {
        var bestPrices = getSideBestPrices(requireNonNull(type));
        if (bestPrices.stream().allMatch(Optional::isPresent)) {
            Preconditions.checkState(bestPrices.stream().allMatch(price -> price.get().getSide() == type));
            return OptionalDouble.of(getOverround(bestPrices.stream()
                    .map(Optional::get).map(Price::getPrice)
                    .collect(Collectors.toList())));
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
        var overround = getOverround(side);
        return overround.stream().map(o -> winnerCount / o).findAny();
    }

    @JsonIgnore
    public OptionalDouble getLastMatchedReciprocal() {
        for (var runnerPrices : getRunnerPrices()) {
            if (runnerPrices.getLastMatchedPrice() == null) return OptionalDouble.empty();
        }
        var prices = getRunnerPrices().stream()
                .map(RunnerPrices::getLastMatchedPrice)
                .collect(Collectors.toList());
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
        var prices = getRunnerPrices().stream()
                .map(rp -> rp.getHomogeneous(side))
                .collect(Collectors.toList());
        return new MarketPrices(winnerCount, market, prices, getTime());
    }

    public List<OptionalDouble> getBestPrices(Side type) {
        return getSideBestPrices(requireNonNull(type)).stream()
                .map(this::getOptionalPrice)
                .collect(Collectors.toList());
    }

    private OptionalDouble getOptionalPrice(Optional<Price> price) {
        return price.map(p -> OptionalDouble.of(p.getPrice()))
                .orElse(OptionalDouble.empty());
    }

    private List<Optional<Price>> getSideBestPrices(Side type) {
        return getHomogeneous(type).getRunnerPrices().stream()
                .map(RunnerPrices::getBestPrice).collect(Collectors.toList());
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

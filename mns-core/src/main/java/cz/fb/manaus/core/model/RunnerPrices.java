package cz.fb.manaus.core.model;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.SortComparator;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static cz.fb.manaus.core.model.PriceComparator.ORDERING;

@Entity
@NamedQueries({
        @NamedQuery(name = RunnerPrices.BY_MARKET_AND_SELECTION,
                query = "select rp from MarketPrices mp inner join mp.runnerPrices rp " +
                        "where mp.market.id = :marketId and rp.selectionId = :selectionId order by mp.time desc")
}
)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class RunnerPrices implements SideMixed<RunnerPrices> {

    public static final String BY_MARKET_AND_SELECTION = "byMarketAndSelection";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private long selectionId;
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @SortComparator(PriceComparator.class)
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    private Collection<Price> prices;

    private Double lastMatchedPrice;

    private Double matchedAmount;

    public RunnerPrices(long selectionId, Collection<Price> prices, Double matched, Double lastMatchedPrice) {
        this.selectionId = selectionId;
        this.prices = prices;
        this.lastMatchedPrice = lastMatchedPrice;
        this.matchedAmount = matched;
    }

    public RunnerPrices() {
    }

    public Optional<Price> getBestPrice() {
        return prices.isEmpty() ? Optional.empty() : Optional.of(ORDERING.min(prices));
    }

    public Integer getId() {
        return id;
    }

    public List<Price> getPricesSorted() {
        return ORDERING.sortedCopy(prices);
    }

    public Collection<Price> getPrices() {
        return prices;
    }

    public void setPrices(Collection<Price> prices) {
        this.prices = prices;
    }

    public long getSelectionId() {
        return selectionId;
    }

    public void setSelectionId(long selectionId) {
        this.selectionId = selectionId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).append(getLastMatchedPrice()).append(getSelectionId()).append(getPricesSorted()).toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RunnerPrices other = (RunnerPrices) obj;
        return new EqualsBuilder()
                .append(getPricesSorted(), other.getPricesSorted())
                .append(getLastMatchedPrice(), other.getLastMatchedPrice())
                .append(getSelectionId(), other.getSelectionId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getPricesSorted())
                .append(getLastMatchedPrice())
                .append(getSelectionId())
                .toHashCode();
    }

    @Override
    public RunnerPrices getHomogeneous(Side side) {
        ImmutableList<Price> prices = FluentIterable.from(this.prices).filter(price -> price.getSide() == side).toList();
        return new RunnerPrices(getSelectionId(), prices, matchedAmount, lastMatchedPrice);
    }

    public Double getLastMatchedPrice() {
        return lastMatchedPrice;
    }

    public Double getMatchedAmount() {
        return matchedAmount;
    }

}
package cz.fb.manaus.core.model;


import com.google.common.base.MoreObjects;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Set;

@Entity
@NamedQueries({
        @NamedQuery(name = BetAction.UPDATE_BET_ID,
                query = "update BetAction ba set ba.betId = :newOne where ba.betId = :oldOne and " +
                        "ba.selectionId = :selectionId and ba.market.id = :marketId"),
        @NamedQuery(name = BetAction.SET_BET_ID,
                query = "update BetAction ba set ba.betId = :betId where ba.id = :actionId")})
public class BetAction {

    public static final String UPDATE_BET_ID = "updateBetIdQuery";
    public static final String SET_BET_ID = "setBetIdQuery";
    public static final String TRADED_VOL_MEAN = "tradedVolumeMean";
    public static final String PROPOSER_PROP = "proposer";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private BetActionType betActionType;
    @Column(nullable = false)
    private Date actionDate;
    @Embedded
    private Price price;
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Market market;
    @Column(nullable = false)
    private long selectionId;
    @Column(unique = true)
    private String betId;
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    private Map<String, String> properties = new HashMap<>();
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    private Set<String> tags = new HashSet<>();
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    private MarketPrices marketPrices;

    public BetAction(BetActionType betActionType, Date actionDate, Price price, Market market, long selectionId) {
        this(betActionType, actionDate, price, market, selectionId, null);
    }

    public BetAction(BetActionType betActionType, Date actionDate, Price price, Market market, long selectionId, String betId) {
        this.betActionType = betActionType;
        this.actionDate = actionDate;
        this.price = price;
        this.market = market;
        this.selectionId = selectionId;
        this.betId = betId;
    }

    public BetAction() {
    }

    public Integer getId() {
        return id;
    }

    public BetActionType getBetActionType() {
        return betActionType;
    }

    public Date getActionDate() {
        return actionDate;
    }

    public Price getPrice() {
        return price;
    }

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public long getSelectionId() {
        return selectionId;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public MarketPrices getMarketPrices() {
        return marketPrices;
    }

    public void setMarketPrices(MarketPrices marketPrices) {
        this.marketPrices = marketPrices;
    }

    public OptionalDouble getDoubleProperty(String propertyName) {
        String strVal = properties.get(propertyName);
        if (strVal == null) return OptionalDouble.empty();
        return OptionalDouble.of(Double.parseDouble(strVal));
    }

    public OptionalInt getIntegerProperty(String propertyName) {
        String strVal = properties.get(propertyName);
        if (strVal == null) return OptionalInt.empty();
        return OptionalInt.of(Integer.parseInt(strVal));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("betActionType", betActionType)
                .add("actionDate", actionDate)
                .add("price", price)
                .add("market", market)
                .add("selectionId", selectionId)
                .add("betId", betId)
                .add("properties", properties)
                .add("tags", tags)
                .toString();
    }
}

package cz.fb.manaus.core.model;


import com.google.common.base.MoreObjects;
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
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;

@Entity
@NamedQueries({
        @NamedQuery(name = BetAction.UPDATE_BET_ID,
                query = "update BetAction ba set ba.betId = :newOne where ba.betId = :oldOne"),
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
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    private MarketPrices marketPrices;

    public static BetAction create(BetActionType betActionType, Date actionDate, Price price,
                                   Market market, long selectionId) {
        var ba = new BetAction();
        ba.setBetActionType(betActionType);
        ba.setActionDate(actionDate);
        ba.setPrice(price);
        ba.setMarket(market);
        ba.setSelectionId(selectionId);
        return ba;
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

    public void setId(Integer id) {
        this.id = id;
    }

    public void setBetActionType(BetActionType betActionType) {
        this.betActionType = betActionType;
    }

    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public void setSelectionId(long selectionId) {
        this.selectionId = selectionId;
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

    public MarketPrices getMarketPrices() {
        return marketPrices;
    }

    public void setMarketPrices(MarketPrices marketPrices) {
        this.marketPrices = marketPrices;
    }

    public OptionalDouble getDoubleProperty(String propertyName) {
        var strVal = Optional.ofNullable(properties.get(propertyName));
        return strVal.stream().mapToDouble(Double::parseDouble).findAny();
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
                .toString();
    }
}

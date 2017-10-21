package cz.fb.manaus.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.Version;
import java.util.Collection;


@Entity
@NamedQuery(name = Market.DELETE_OLDER_THAN, query = "delete from Market m where m.event.openDate < :date")
public class Market {

    public static final String DELETE_OLDER_THAN = "M_DELETE_OLDER_THAN";

    @Id
    private String id;
    @Version
    private int version;

    @Column(nullable = false)
    private String name;

    @Column
    private Double matchedAmount;

    @JsonIgnore
    @Column(nullable = false)
    private boolean bspMarket;

    @Column(nullable = false)
    private boolean inPlay;

    @Column
    private String type;


    @Embedded
    private EventType eventType;
    @Embedded
    private Competition competition;
    @Embedded
    private Event event;

    @ElementCollection(fetch = FetchType.EAGER)
    @OrderBy("sortPriority ASC")
    @Fetch(FetchMode.SELECT)
    private Collection<Runner> runners;

    public Market() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMatchedAmount() {
        return matchedAmount;
    }

    public void setMatchedAmount(Double matchedAmount) {
        this.matchedAmount = matchedAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isBspMarket() {
        return bspMarket;
    }

    public void setBspMarket(boolean bspMarket) {
        this.bspMarket = bspMarket;
    }

    public boolean isInPlay() {
        return inPlay;
    }

    public void setInPlay(boolean inPlay) {
        this.inPlay = inPlay;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public Collection<Runner> getRunners() {
        return runners;
    }

    public void setRunners(Collection<Runner> runners) {
        this.runners = runners;
    }

    public Runner getRunner(long selectionId) {
        return getRunners().stream()
                .filter(r -> r.getSelectionId() == selectionId)
                .findFirst()
                .get();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        Market other = (Market) obj;
        return new EqualsBuilder()
                .append(getId(), other.getId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getId())
                .toHashCode();
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("version", version)
                .add("name", name)
                .add("matchedAmount", matchedAmount)
                .add("bspMarket", bspMarket)
                .add("inPlay", inPlay)
                .add("type", type)
                .add("eventType", eventType)
                .add("competition", competition)
                .add("event", event)
                .add("runners", runners)
                .toString();
    }
}

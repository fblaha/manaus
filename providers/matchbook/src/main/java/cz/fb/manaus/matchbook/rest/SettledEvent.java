package cz.fb.manaus.matchbook.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.Date;
import java.util.List;

public class SettledEvent {

    private long id;
    private String name;
    private Date start;
    private String status;
    @JsonProperty("sport-id")
    private long sportId;
    @JsonProperty("sport-url")
    private String sportUrl;
    @JsonProperty("sport-name")
    private String sportName;
    @JsonProperty("start-time")
    private Date startTime;
    @JsonProperty("finished-dead-heat")
    private boolean finishedDeadHeat;
    private List<SettledMarket> markets;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getSportId() {
        return sportId;
    }

    public void setSportId(long sportId) {
        this.sportId = sportId;
    }

    public String getSportUrl() {
        return sportUrl;
    }

    public void setSportUrl(String sportUrl) {
        this.sportUrl = sportUrl;
    }

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public boolean isFinishedDeadHeat() {
        return finishedDeadHeat;
    }

    public void setFinishedDeadHeat(boolean finishedDeadHeat) {
        this.finishedDeadHeat = finishedDeadHeat;
    }

    public List<SettledMarket> getMarkets() {
        return markets;
    }

    public void setMarkets(List<SettledMarket> markets) {
        this.markets = markets;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("start", start)
                .add("status", status)
                .add("sportId", sportId)
                .add("sportUrl", sportUrl)
                .add("sportName", sportName)
                .add("startTime", startTime)
                .add("finishedDeadHeat", finishedDeadHeat)
                .add("markets", markets)
                .toString();
    }
}

package cz.fb.manaus.matchbook.rest;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.Date;
import java.util.List;

public class Market {
    @JsonProperty("event-id")
    private long eventId;
    private long id;
    private double handicap;
    private String name;

    @JsonProperty("runner-ids")
    private List<Integer> runnerIds;
    private List<Runner> runners;
    private Date start;
    private String status;
    private String type;
    @JsonProperty("grading-type")
    private String gradingType;
    @JsonProperty("in-running-flag")
    private boolean inRunningFlag;
    @JsonProperty("allow-live-betting")
    private boolean allowLiveBetting;
    @JsonProperty("asian-handicap")
    private String asianHandicap;


    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getHandicap() {
        return handicap;
    }

    public void setHandicap(double handicap) {
        this.handicap = handicap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getRunnerIds() {
        return runnerIds;
    }

    public void setRunnerIds(List<Integer> runnerIds) {
        this.runnerIds = runnerIds;
    }

    public List<Runner> getRunners() {
        return runners;
    }

    public void setRunners(List<Runner> runners) {
        this.runners = runners;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGradingType() {
        return gradingType;
    }

    public void setGradingType(String gradingType) {
        this.gradingType = gradingType;
    }

    public boolean isInRunningFlag() {
        return inRunningFlag;
    }

    public void setInRunningFlag(boolean inRunningFlag) {
        this.inRunningFlag = inRunningFlag;
    }

    public boolean isAllowLiveBetting() {
        return allowLiveBetting;
    }

    public void setAllowLiveBetting(boolean allowLiveBetting) {
        this.allowLiveBetting = allowLiveBetting;
    }

    public String getAsianHandicap() {
        return asianHandicap;
    }

    public void setAsianHandicap(String asianHandicap) {
        this.asianHandicap = asianHandicap;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("eventId", eventId)
                .add("id", id)
                .add("handicap", handicap)
                .add("name", name)
                .add("runnerIds", runnerIds)
                .add("runners", runners)
                .add("start", start)
                .add("status", status)
                .add("type", type)
                .add("gradingType", gradingType)
                .add("inRunningFlag", inRunningFlag)
                .add("allowLiveBetting", allowLiveBetting)
                .add("asianHandicap", asianHandicap)
                .toString();
    }
}

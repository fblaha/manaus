package cz.fb.manaus.betfair.rest;

import com.google.common.base.MoreObjects;

public class RunnerCatalog {

    private long selectionId;
    private String runnerName;
    private double handicap;
    private int sortPriority;

    public long getSelectionId() {
        return selectionId;
    }

    public void setSelectionId(long selectionId) {
        this.selectionId = selectionId;
    }

    public String getRunnerName() {
        return runnerName;
    }

    public void setRunnerName(String runnerName) {
        this.runnerName = runnerName;
    }

    public double getHandicap() {
        return handicap;
    }

    public void setHandicap(double handicap) {
        this.handicap = handicap;
    }

    public int getSortPriority() {
        return sortPriority;
    }

    public void setSortPriority(int sortPriority) {
        this.sortPriority = sortPriority;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("selectionId", selectionId)
                .add("sortPriority", sortPriority)
                .add("runnerName", runnerName)
                .add("handicap", handicap)
                .toString();
    }
}

package cz.fb.manaus.core.model;

import com.google.common.base.MoreObjects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Runner {


    @Column(nullable = false)
    private long selectionId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double handicap;

    @Column(nullable = false)
    private int sortPriority;

    public static Runner create(long selectionId, String name, double handicap, int sortPriority) {
        var runner = new Runner();
        runner.setSelectionId(selectionId);
        runner.setName(name);
        runner.setHandicap(handicap);
        runner.setSortPriority(sortPriority);
        return runner;
    }

    public long getSelectionId() {
        return selectionId;
    }

    public void setSelectionId(long selectionId) {
        this.selectionId = selectionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
                .add("name", name)
                .add("handicap", handicap)
                .add("sortPriority", sortPriority)
                .toString();
    }
}

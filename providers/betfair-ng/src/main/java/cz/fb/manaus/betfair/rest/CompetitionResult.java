package cz.fb.manaus.betfair.rest;

import com.google.common.base.MoreObjects;

public class CompetitionResult {
    private Competition competition;
    private int marketCount;
    private String competitionRegion;

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public int getMarketCount() {
        return marketCount;
    }

    public void setMarketCount(int marketCount) {
        this.marketCount = marketCount;
    }

    public String getCompetitionRegion() {
        return competitionRegion;
    }

    public void setCompetitionRegion(String competitionRegion) {
        this.competitionRegion = competitionRegion;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("competition", competition)
                .add("marketCount", marketCount)
                .add("competitionRegion", competitionRegion)
                .toString();
    }
}

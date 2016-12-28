package cz.fb.manaus.betfair.rest;

import com.google.common.base.MoreObjects;

import java.util.List;

public class PlaceExecutionReport extends AbstractExecutionReport<PlaceInstructionReport> {

    private List<PlaceInstructionReport> instructionReports;

    @Override
    public List<PlaceInstructionReport> getInstructionReports() {
        return instructionReports;
    }

    public void setInstructionReports(
            List<PlaceInstructionReport> instructionReports) {
        this.instructionReports = instructionReports;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("status", status)
                .add("errorCode", errorCode)
                .add("marketId", marketId)
                .add("instructionReports", instructionReports)
                .toString();
    }
}

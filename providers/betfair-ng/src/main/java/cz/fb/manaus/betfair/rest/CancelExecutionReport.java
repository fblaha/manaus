package cz.fb.manaus.betfair.rest;

import java.util.List;

public class CancelExecutionReport extends AbstractExecutionReport<CancelInstructionReport> {
    private List<CancelInstructionReport> instructionReports;

    @Override
    public List<CancelInstructionReport> getInstructionReports() {
        return instructionReports;
    }

    public void setInstructionReports(List<CancelInstructionReport> instructionReports) {
        this.instructionReports = instructionReports;
    }

}

package cz.fb.manaus.betfair.rest;

import java.util.List;

public class ReplaceExecutionReport extends AbstractExecutionReport<ReplaceInstructionReport> {
    private List<ReplaceInstructionReport> instructionReports;

    @Override
    public List<ReplaceInstructionReport> getInstructionReports() {
        return instructionReports;
    }

    public void setInstructionReports(List<ReplaceInstructionReport> instructionReports) {
        this.instructionReports = instructionReports;
    }

}

package cz.fb.manaus.betfair.rest;

public class ReplaceInstructionReport extends AbstractInstructionReport {
    private PlaceInstructionReport placeInstructionReport;
    private CancelInstructionReport cancelInstructionReport;

    public PlaceInstructionReport getPlaceInstructionReport() {
        return placeInstructionReport;
    }

    public void setPlaceInstructionReport(PlaceInstructionReport placeInstructionReport) {
        this.placeInstructionReport = placeInstructionReport;
    }

    public CancelInstructionReport getCancelInstructionReport() {
        return cancelInstructionReport;
    }

    public void setCancelInstructionReport(CancelInstructionReport cancelInstructionReport) {
        this.cancelInstructionReport = cancelInstructionReport;
    }
}

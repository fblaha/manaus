package cz.fb.manaus.betfair.rest;

import com.google.common.base.MoreObjects;

import java.util.Date;

public class PlaceInstructionReport extends AbstractInstructionReport {
    private PlaceInstruction instruction;
    private String betId;
    private Date placedDate;
    private double averagePriceMatched;
    private double sizeMatched;


    public PlaceInstruction getInstruction() {
        return instruction;
    }

    public void setInstruction(PlaceInstruction instruction) {
        this.instruction = instruction;
    }

    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }

    public Date getPlacedDate() {
        return placedDate;
    }

    public void setPlacedDate(Date placedDate) {
        this.placedDate = placedDate;
    }

    public double getAveragePriceMatched() {
        return averagePriceMatched;
    }

    public void setAveragePriceMatched(double averagePriceMatched) {
        this.averagePriceMatched = averagePriceMatched;
    }

    public double getSizeMatched() {
        return sizeMatched;
    }

    public void setSizeMatched(double sizeMatched) {
        this.sizeMatched = sizeMatched;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("status", status)
                .add("errorCode", errorCode)
                .add("instruction", instruction)
                .add("betId", betId)
                .add("placedDate", placedDate)
                .add("averagePriceMatched", averagePriceMatched)
                .add("sizeMatched", sizeMatched)
                .toString();
    }
}

package cz.fb.manaus.betfair.rest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Date;

public class CancelInstructionReport extends AbstractInstructionReport {
    private CancelInstruction instruction;
    private double sizeCancelled;
    @JsonDeserialize(using = DateDeserializer.class)
    private Date cancelledDate;


    public CancelInstruction getInstruction() {
        return instruction;
    }

    public void setInstruction(CancelInstruction instruction) {
        this.instruction = instruction;
    }


    public double getSizeCancelled() {
        return sizeCancelled;
    }

    public void setSizeCancelled(double sizeCancelled) {
        this.sizeCancelled = sizeCancelled;
    }

    public Date getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(Date cancelledDate) {
        this.cancelledDate = cancelledDate;
    }
}

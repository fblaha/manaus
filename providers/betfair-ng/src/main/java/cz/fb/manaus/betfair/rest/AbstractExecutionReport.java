package cz.fb.manaus.betfair.rest;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

abstract public class AbstractExecutionReport<T extends ValidationAware> implements ValidationAware, InstructionReportAware<T> {
    protected String marketId;
    protected ExecutionReportStatus status;
    protected ExecutionReportErrorCode errorCode;

    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public ExecutionReportStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionReportStatus status) {
        this.status = status;
    }

    public ExecutionReportErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ExecutionReportErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public void validate() {
        Preconditions.checkState(status == ExecutionReportStatus.SUCCESS, errorCode);
        if (getInstructionReports() != null) {
            getInstructionReports().forEach(ValidationAware::validate);
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("marketId", marketId)
                .add("status", status)
                .add("errorCode", errorCode)
                .add("instructionReports", getInstructionReports())
                .toString();
    }

}

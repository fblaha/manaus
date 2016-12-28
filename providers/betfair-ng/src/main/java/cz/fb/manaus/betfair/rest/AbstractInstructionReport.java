package cz.fb.manaus.betfair.rest;

import com.google.common.base.Preconditions;

abstract public class AbstractInstructionReport implements ValidationAware {
    protected InstructionReportStatus status;
    protected InstructionReportErrorCode errorCode;

    public InstructionReportStatus getStatus() {
        return status;
    }

    public void setStatus(InstructionReportStatus status) {
        this.status = status;
    }

    public InstructionReportErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(InstructionReportErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public void validate() {
        Preconditions.checkState(status == InstructionReportStatus.SUCCESS, errorCode);
    }
}

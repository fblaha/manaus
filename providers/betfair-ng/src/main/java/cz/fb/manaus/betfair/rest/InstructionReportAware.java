package cz.fb.manaus.betfair.rest;

import java.util.List;

public interface InstructionReportAware<T extends ValidationAware> {

    List<T> getInstructionReports();

}

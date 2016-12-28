package cz.fb.manaus.rest;

import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.RunnerPrices;

import java.util.List;

public class BetStory {
    private BetAction action;
    private RunnerPrices runnerPrices;
    private List<BetAction> previousActions;

    public BetStory(BetAction action, RunnerPrices runnerPrices, List<BetAction> previousActions) {
        this.action = action;
        this.runnerPrices = runnerPrices;
        this.previousActions = previousActions;
    }

    public BetStory() {
    }

    public BetAction getAction() {
        return action;
    }

    public void setAction(BetAction action) {
        this.action = action;
    }

    public RunnerPrices getRunnerPrices() {
        return runnerPrices;
    }

    public void setRunnerPrices(RunnerPrices runnerPrices) {
        this.runnerPrices = runnerPrices;
    }

    public List<BetAction> getPreviousActions() {
        return previousActions;
    }

    public void setPreviousActions(List<BetAction> previousActions) {
        this.previousActions = previousActions;
    }
}

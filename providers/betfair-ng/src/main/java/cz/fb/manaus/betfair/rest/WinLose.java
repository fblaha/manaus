package cz.fb.manaus.betfair.rest;

public enum WinLose {
    RESULT_LOST(true),
    RESULT_WON(true),
    RESULT_FIX(false),
    RESULT_ERR(false),
    RESULT_NOT_APPLICABLE(false);

    private final boolean significant;

    WinLose(boolean significant) {
        this.significant = significant;
    }

    public boolean isSignificant() {
        return significant;
    }
}

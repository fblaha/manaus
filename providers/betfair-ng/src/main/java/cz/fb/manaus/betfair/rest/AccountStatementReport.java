package cz.fb.manaus.betfair.rest;

import com.google.common.base.MoreObjects;

import java.util.List;

public class AccountStatementReport {

    private List<AccountStatement> accountStatement;
    private boolean moreAvailable;

    public List<AccountStatement> getAccountStatement() {
        return accountStatement;
    }

    public void setAccountStatement(List<AccountStatement> accountStatement) {
        this.accountStatement = accountStatement;
    }

    public boolean isMoreAvailable() {
        return moreAvailable;
    }

    public void setMoreAvailable(boolean moreAvailable) {
        this.moreAvailable = moreAvailable;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("accountStatement", accountStatement)
                .add("moreAvailable", moreAvailable)
                .toString();
    }
}

package cz.fb.manaus.reactor.betting.listener;

import cz.fb.manaus.core.model.AccountMoney;
import cz.fb.manaus.core.model.MarketSnapshot;
import cz.fb.manaus.reactor.betting.BetCollector;

import java.util.Optional;
import java.util.Set;

public interface MarketSnapshotListener {

    void onMarketSnapshot(MarketSnapshot snapshot,
                          BetCollector betCollector,
                          Optional<AccountMoney> accountMoney,
                          Set<String> categoryBlackList);

}

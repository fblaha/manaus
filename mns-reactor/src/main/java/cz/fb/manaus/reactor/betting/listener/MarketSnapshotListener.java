package cz.fb.manaus.reactor.betting.listener;

import cz.fb.manaus.core.model.CollectedBets;
import cz.fb.manaus.core.model.MarketSnapshot;
import cz.fb.manaus.reactor.betting.BetCollector;

public interface MarketSnapshotListener {

    void onMarketSnapshot(MarketSnapshot snapshot, BetCollector betCollector, CollectedBets collectedBets);

}

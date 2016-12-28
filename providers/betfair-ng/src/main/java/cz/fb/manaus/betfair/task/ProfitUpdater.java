package cz.fb.manaus.betfair.task;

import cz.fb.manaus.betfair.BetfairFacade;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.provider.ProviderTask;
import cz.fb.manaus.core.settlement.SaveStatus;
import cz.fb.manaus.core.settlement.SettledBetSaver;
import cz.fb.manaus.spring.DatabaseComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@DatabaseComponent
public class ProfitUpdater implements ProviderTask {
    @Autowired
    private BetfairFacade betService;
    @Autowired
    private SettledBetSaver betSaver;

    @Override
    public String getName() {
        return "betfair.profit.update";
    }

    @Override
    public Duration getPauseDuration() {
        return Duration.ofMinutes(10);
    }

    @Override
    public void execute() {
        for (int i = 0; i < 5; i++) {
            Set<SaveStatus> saveStatuses = EnumSet.noneOf(SaveStatus.class);
            Map<String, SettledBet> settledBets = betService.getSettledBets(i * 200, 200);
            for (Map.Entry<String, SettledBet> entry : settledBets.entrySet()) {
                saveStatuses.add(betSaver.saveBet(entry.getKey(), entry.getValue()));
            }
            if (saveStatuses.contains(SaveStatus.COLLISION) || !saveStatuses.contains(SaveStatus.OK)) {
                return;
            }
        }
    }
}

package cz.fb.manaus.matchbook.task;

import cz.fb.manaus.core.provider.ProviderTask;
import cz.fb.manaus.core.settlement.SettledBetSaver;
import cz.fb.manaus.matchbook.MatchbookService;
import cz.fb.manaus.spring.DatabaseComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@DatabaseComponent
public class SettledBetFetcher implements ProviderTask {
    @Autowired
    private MatchbookService betService;
    @Autowired
    private SettledBetSaver betSaver;

    @Override
    public String getName() {
        return "matchbook.profit.update";
    }

    @Override
    public Duration getPauseDuration() {
        return Duration.ofMinutes(30);
    }

    @Override
    public void execute() {
        betService.walkSettledBets(Instant.now().minus(7, ChronoUnit.DAYS), betSaver::saveBet);
    }
}

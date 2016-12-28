package cz.fb.manaus.matchbook.task;

import cz.fb.manaus.core.dao.BetActionDao;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.provider.ProviderTask;
import cz.fb.manaus.core.settlement.SettledBetSaver;
import cz.fb.manaus.matchbook.MatchbookService;
import cz.fb.manaus.matchbook.rest.Settlement;
import cz.fb.manaus.reactor.betting.action.BetUtils;
import cz.fb.manaus.reactor.rounding.MatchbookRoundingPlugin;
import cz.fb.manaus.spring.DatabaseComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.collect.FluentIterable.from;

@DatabaseComponent
public class SettledBetFetcher implements ProviderTask {
    private static final Logger log = Logger.getLogger(SettledBetFetcher.class.getSimpleName());
    @Autowired
    private MatchbookService betService;
    @Autowired
    private SettledBetSaver betSaver;
    @Autowired
    private BetActionDao actionDao;
    @Autowired
    private BetUtils betUtils;
    @Autowired
    private MatchbookRoundingPlugin roundingPlugin;

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
        betService.walkSettlements(Instant.now().minus(7, ChronoUnit.DAYS), this::fetchAndSave);
    }

    private void fetchAndSave(Settlement settlement) {
        long marketId = settlement.getMarketId();
        List<BetAction> actions = actionDao.getBetActions(Long.toString(marketId), OptionalLong.empty(), Optional.empty());
        for (long selectionId : from(actions).transform(BetAction::getSelectionId).toSet()) {
            List<SettledBet> settledBets = betService.getSettledBets(marketId, selectionId);
            for (SettledBet settledBet : settledBets) {
                double step = roundingPlugin.getStep(settledBet.getPrice().getPrice());
                // TODO verify action matching
                Optional<BetAction> betAction = betUtils.findBestMatchingAction(settledBet, step / 2, actions);
                if (betAction.isPresent()) {
                    BetAction action = betAction.get();
                    settledBet.setBetAction(action);
                    if (settledBet.getPrice().getSide() == null) {
                        log.log(Level.WARNING, "Missing price side - setting value from action on ''{0}''", settledBet);
                        settledBet.getPrice().setSide(settledBet.getBetAction().getPrice().getSide());
                    }
                    betSaver.saveBet(action.getBetId(), settledBet);
                } else {
                    log.log(Level.WARNING, "Unable to find matching bet action ''{0}'' - ''{1}''",
                            new Object[]{settledBet, settlement});
                }
            }
        }
    }

}

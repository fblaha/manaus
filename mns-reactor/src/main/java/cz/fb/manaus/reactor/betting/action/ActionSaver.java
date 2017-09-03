package cz.fb.manaus.reactor.betting.action;

import com.google.common.base.Joiner;
import cz.fb.manaus.core.dao.BetActionDao;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.service.PropertiesService;
import cz.fb.manaus.spring.DatabaseComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static cz.fb.manaus.core.category.categorizer.WeekDayCategorizer.getWeekDay;

@Deprecated
@DatabaseComponent
public class ActionSaver {

    public static final String PROPOSER_STATS = "proposer.stats";
    private static final Logger log = Logger.getLogger(ActionSaver.class.getSimpleName());
    @Autowired
    private BetActionDao betActionDao;
    @Autowired
    private PropertiesService service;
    @Autowired
    private BetUtils betUtils;

    private void saveBet(BetAction action, String betId) {
        Date date = new Date();
        String proposers = action.getProperties().get(BetAction.PROPOSER_PROP);
        String side = action.getPrice().getSide().name().toLowerCase();
        for (String proposer : betUtils.parseProposers(proposers)) {
            String key = Joiner.on('.').join(PROPOSER_STATS, getWeekDay(date), side, proposer);
            service.incrementAntGet(key, Duration.ofDays(1));
        }
        action.setBetId(betId);
        replaceExistingBetId(action);
        betActionDao.saveOrUpdate(action);
    }

    private void replaceExistingBetId(BetAction action) {
        long time = Instant.now().getEpochSecond();
        String previousBetId = action.getBetId() + "_" + Long.toHexString(time);
        String marketId = action.getMarket().getId();
        int updatedCount = betActionDao.updateBetId(action.getBetId(), previousBetId, marketId, action.getSelectionId());
        if (updatedCount > 0) {
            log.log(Level.INFO, "Previous action bet id set to ''{0}''", previousBetId);
        }
    }

    public Consumer<String> actionSaver(BetAction action) {
        return new BetActionSaver(action);
    }

    private class BetActionSaver implements Consumer<String> {
        private final BetAction action;

        public BetActionSaver(BetAction action) {
            this.action = action;
        }

        @Override
        public void accept(String betId) {
            saveBet(action, betId);
        }
    }
}

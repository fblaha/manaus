package cz.fb.manaus.reactor.betting.action;

import com.google.common.base.Joiner;
import cz.fb.manaus.core.dao.BetActionDao;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.service.PropertiesService;
import cz.fb.manaus.spring.DatabaseComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkState;
import static cz.fb.manaus.core.category.categorizer.WeekDayCategorizer.getWeekDay;

@DatabaseComponent
public class ActionSaver {

    public static final String PROPOSER_STATS = "proposer.stats";
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
        betActionDao.getBetAction(betId).ifPresent(stored -> {
            checkState(Objects.equals(stored.getBetId(), action.getBetId()));
            checkState(stored.getSelectionId() == action.getSelectionId());
            Date actionDate = stored.getActionDate();
            long time = actionDate.getTime();
            stored.setBetId(betId + "_" + Long.toHexString(time));
            betActionDao.saveOrUpdate(stored);
        });
        betActionDao.saveOrUpdate(action);
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

package cz.fb.manaus.reactor.betting.action;

import cz.fb.manaus.core.dao.BetActionDao;
import cz.fb.manaus.core.dao.MarketPricesDao;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.service.PropertiesService;
import cz.fb.manaus.spring.DatabaseComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

@DatabaseComponent
public class ActionSaver {

    private static final Logger log = Logger.getLogger(ActionSaver.class.getSimpleName());
    @Autowired
    private BetActionDao betActionDao;
    @Autowired
    private MarketPricesDao pricesDao;
    @Autowired
    private PropertiesService service;
    @Autowired
    private BetUtils betUtils;

    public int setBetId(String betId, int actionId) {
        replaceExistingBetId(betId);
        return betActionDao.setBetId(actionId, betId);
    }

    public void saveAction(BetAction action) {
        MarketPrices prices = action.getMarketPrices();
        if (!Optional.ofNullable(prices.getId()).isPresent()) {
            pricesDao.saveOrUpdate(prices);
            requireNonNull(prices.getId());
        }
        betActionDao.saveOrUpdate(action);
    }

    private void replaceExistingBetId(String betId) {
        long time = Instant.now().getEpochSecond();
        String previousBetId = betId + "_" + Long.toHexString(time);
        int updatedCount = betActionDao.updateBetId(betId, previousBetId);
        if (updatedCount > 0) {
            log.log(Level.INFO, "Previous action bet id set to ''{0}''", previousBetId);
        }
    }

}

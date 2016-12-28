package cz.fb.manaus.core.settlement;

import cz.fb.manaus.core.dao.BetActionDao;
import cz.fb.manaus.core.dao.MarketDao;
import cz.fb.manaus.core.dao.SettledBetDao;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class SettledBetSaver {
    private static final Logger log = Logger.getLogger(SettledBetSaver.class.getSimpleName());
    @Autowired
    private SettledBetDao settledBetDao;
    @Autowired
    private BetActionDao betActionDao;
    @Autowired
    private MarketDao marketDao;

    @Transactional
    public SaveStatus saveBet(String betId, final SettledBet settledBet) {
        if (!settledBetDao.getSettledBet(betId).isPresent()) {
            if (settledBet.getBetAction() == null) {
                settledBet.setBetAction(betActionDao.getBetAction(betId).orElse(null));
            }
            if (settledBet.getBetAction() != null) {
                validate(settledBet);
                settledBetDao.saveOrUpdate(settledBet);
                return SaveStatus.OK;
            } else {
                log.log(Level.WARNING, "SETTLED_BET: no bet action for ''{0}''", settledBet);
                return SaveStatus.NO_ACTION;
            }
        } else {
            log.log(Level.INFO, "SETTLED_BET: action with id ''{0}'' already saved", betId);
            return SaveStatus.COLLISION;
        }
    }

    private void validate(SettledBet bet) {
        Date betDate = bet.getPlaced();
        Date actionDate = bet.getBetAction().getActionDate();
        Date openDate = bet.getBetAction().getMarket().getEvent().getOpenDate();
        long latency = actionDate.toInstant().until(betDate.toInstant(), ChronoUnit.SECONDS);
        if (latency > 30) {
            log.log(Level.WARNING, "Too big latency for ''{0}''", bet);
        }
        if (betDate.after(openDate)) {
            log.log(Level.SEVERE, "Placed after open date ''{0}''", bet);
        }
        Price requestedPrice = bet.getBetAction().getPrice();
        Price price = bet.getPrice();
        if (!Price.priceEq(requestedPrice.getPrice(), price.getPrice())) {
            log.log(Level.WARNING, "Different requested price ''{0}''", bet);
        }
    }


}

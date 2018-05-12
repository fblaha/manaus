package cz.fb.manaus.core.settlement;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Preconditions;
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
import java.util.Optional;
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
    @Autowired
    private MetricRegistry metricRegistry;

    @Transactional
    public SaveStatus saveBet(String betId, SettledBet settledBet) {
        if (!settledBetDao.getSettledBet(betId).isPresent()) {
            settledBet.setBetAction(betActionDao.getBetAction(betId).orElse(null));
            if (settledBet.getBetAction() != null) {
                validate(settledBet);
                settledBetDao.saveOrUpdate(settledBet);
                metricRegistry.counter("settled.bet.new").inc();
                return SaveStatus.OK;
            } else {
                metricRegistry.counter("settled.bet.NO_ACTION").inc();
                log.log(Level.WARNING, "SETTLED_BET: no bet action for ''{0}''", settledBet);
                return SaveStatus.NO_ACTION;
            }
        } else {
            log.log(Level.INFO, "SETTLED_BET: action with id ''{0}'' already saved", betId);
            return SaveStatus.COLLISION;
        }
    }

    private void validate(SettledBet bet) {
        validateTimes(bet);
        validatePrice(bet);
        validateSelection(bet);
    }

    private void validatePrice(SettledBet bet) {
        Price requestedPrice = bet.getBetAction().getPrice();
        Price price = bet.getPrice();
        if (!Price.priceEq(requestedPrice.getPrice(), price.getPrice())) {
            log.log(Level.WARNING, "Different requested price ''{0}''", bet);
        }
    }

    private void validateSelection(SettledBet bet) {
        long selectionId = bet.getBetAction().getSelectionId();
        Preconditions.checkArgument(selectionId == bet.getSelectionId(),
                "action.selectionId != bet.selectionId");
    }

    private void validateTimes(SettledBet bet) {
        Optional.ofNullable(bet.getPlaced()).ifPresent(placed -> {
            Date actionDate = bet.getBetAction().getActionDate();
            Date openDate = bet.getBetAction().getMarket().getEvent().getOpenDate();
            long latency = actionDate.toInstant().until(placed.toInstant(), ChronoUnit.SECONDS);
            if (latency > 30) {
                log.log(Level.WARNING, "Too big latency for ''{0}''", bet);
            }
            if (placed.after(openDate)) {
                metricRegistry.counter("settled.bet.PLACED_AFTER_START").inc();
                log.log(Level.SEVERE, "Placed after open date ''{0}''", bet);
            }
        });
    }


}

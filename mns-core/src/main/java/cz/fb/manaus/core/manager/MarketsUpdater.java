package cz.fb.manaus.core.manager;

import cz.fb.manaus.core.dao.MarketDao;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.spring.DatabaseComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@DatabaseComponent
public class MarketsUpdater {
    private static final Logger log = Logger.getLogger(MarketsUpdater.class.getSimpleName());

    @Autowired
    private MarketDao dao;
    @Autowired
    private MarketFilterService filterService;

    public boolean checkAndSave(Market newMarket) {
        Optional<Market> market = dao.get(newMarket.getId());
        if (market.isPresent() || filterService.accept(newMarket)) {
            try {
                log.log(Level.INFO, "Saving/updating market ''{0}''", newMarket.getId());
                dao.saveOrUpdate(newMarket);
                return true;
            } catch (RuntimeException exception) {
                log.log(Level.SEVERE, "save/merge failed, new market ''{0}''", newMarket);
                log.log(Level.SEVERE, "fix it!", exception);
            }
        }
        return false;
    }

}

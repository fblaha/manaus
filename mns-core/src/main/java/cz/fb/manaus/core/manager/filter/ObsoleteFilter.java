package cz.fb.manaus.core.manager.filter;

import cz.fb.manaus.core.model.Market;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class ObsoleteFilter implements MarketFilter {

    private static final Logger log = Logger.getLogger(ObsoleteFilter.class.getSimpleName());

    @Override
    public boolean accept(Market market, Set<String> categoryBlacklist) {
        var result = market.getEvent().getOpenDate().after(new Date());
        if (!result) {
            log.log(Level.FINEST, "Omitting obsolete date ''{0}'' for ''{1}''", new Object[]{market.getEvent().getOpenDate(), market});
        }
        return result;
    }

    @Override
    public boolean isStrict() {
        return true;
    }
}
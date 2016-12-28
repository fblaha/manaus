package cz.fb.manaus.betfair;

import cz.fb.manaus.core.model.MarketSnapshot;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.test.AbstractRemoteTestCase;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class BetfairFacadeTest extends AbstractRemoteTestCase {

    @Autowired
    private BetfairFacade betfairFacade;

    @Test
    public void testFetchMarkets() throws Exception {
        betfairFacade.walkMarkets(new Date(), DateUtils.addDays(new Date(), 2), market -> {
            Map<String, MarketSnapshot> snapshot = betfairFacade.getSnapshot(Collections.singleton(market.getId()));
            System.out.println("snapshot = " + snapshot);
        });
    }

    @Test
    public void testSettledBets() throws Exception {
        Map<String, SettledBet> settledBets = betfairFacade.getSettledBets(0, 50);
        settledBets.values().forEach(System.out::println);
    }

    @Test
    public void testSnapshot() throws Exception {
        Map<String, MarketSnapshot> snapshot = betfairFacade.getSnapshot(Collections.singleton("1.115036515"));
        System.out.println("snapshot = " + snapshot);

    }

}
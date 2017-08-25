package cz.fb.manaus.betfair;

import cz.fb.manaus.betfair.rest.AbstractExecutionReport;
import cz.fb.manaus.betfair.rest.PlaceExecutionReport;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractRemoteTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.Collections.singletonList;

public class RestBetfairServiceTest extends AbstractRemoteTestCase {

    @Autowired
    private RestBetfairService service;

    @Test
    public void testPlaceBet() throws Exception {
        PlaceExecutionReport bet = service.placeBets(singletonList(new Bet(null, "1.123450983", 5026012, new Price(5.07d, 2d, Side.BACK), null, 0d)));
        System.out.println("bet = " + bet);
    }

    @Test
    public void testReplaceBet() throws Exception {
        AbstractExecutionReport bet = service.replaceBets(singletonList(new Bet("45446464", "1.114786454", 1234639L, new Price(2d, 2d, Side.BACK), null, 0d)));
        System.out.println("bet = " + bet);
    }

    @Test
    public void testCancelBet() throws Exception {
        AbstractExecutionReport bet = service.cancelBets(singletonList(new Bet("45333414548", "1.116946265", 1234639L, null, null, 0d)));
        System.out.println("bet = " + bet);
    }

}
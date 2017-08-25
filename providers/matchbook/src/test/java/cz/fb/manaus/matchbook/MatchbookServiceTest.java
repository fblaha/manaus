package cz.fb.manaus.matchbook;

import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractRemoteTestCase;
import cz.fb.manaus.reactor.betting.BetEndpoint;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static cz.fb.manaus.spring.CoreLocalConfiguration.TEST_PROFILE;

@ActiveProfiles(value = {"matchbook", TEST_PROFILE}, inheritProfiles = false)
public class MatchbookServiceTest extends AbstractRemoteTestCase {

    @Autowired
    private MatchbookService service;

    @Test
    public void testWalk() throws Exception {
        service.walkMarkets(Instant.now(), Instant.now().plus(5, ChronoUnit.DAYS), System.out::println);
    }

    @Test
    public void testPlace() throws Exception {
        Bet bet = new Bet(null, "1315615", 2494510, new Price(1.63458, 2, Side.LAY), null, 0);
        List<String> ids = service.placeBets(BetEndpoint.devNull(), Collections.singletonList(bet));
        System.out.println("ids = " + ids);
    }

    @Test
    public void testUpdate() throws Exception {
        Price price = new Price(6, 2, Side.BACK);
        Bet bet = new Bet("412514648480014", "411264087370009", 411264087490009L,
                price, null, 0);
        List<String> ids = service.placeBets(BetEndpoint.devNull(), Collections.singletonList(bet));
        System.out.println("ids = " + ids);
    }

    @Test
    public void testSettled() throws Exception {
        service.walkSettledBets(Instant.now().minus(1, ChronoUnit.DAYS),
                (s, settledBet) -> System.out.println(s + " : " + settledBet));
    }
}
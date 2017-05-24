package cz.fb.manaus.rest;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration(classes = ProfitController.class)
public class ProfitControllerTest extends AbstractControllerTest {

    @Test
    public void testProfitRecords() throws Exception {
        createMarketWithSingleSettledBet();
        checkResponse("/profit/1d", "category", "side_lay", "profit");
    }

    @Test
    public void testProgressRecords() throws Exception {
        createMarketWithSingleSettledBet();
        checkResponse("/fc-progress/1d", "category", "actualMatched", "actualMatched", "fairnessBack");
    }

    @Test
    public void testCoverageRecords() throws Exception {
        createMarketWithSingleSettledBet();
        checkResponse("/fc-coverage/1d", "placedAhead_solo");
    }

    @Test
    public void testProgressSingleFunction() throws Exception {
        createMarketWithSingleSettledBet();
        checkResponse("/fc-progress/1d?function=actualMatched", "actualMatched");
    }

}
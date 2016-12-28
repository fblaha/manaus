package cz.fb.manaus.rest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration(classes = SettledBetController.class)
public class SettledBetControllerTest extends AbstractControllerTest {

    @Before
    public void setUp() throws Exception {
        createMarketWithSingleSettledBet();
    }

    @Test
    public void testBetList() throws Exception {
        checkResponse("/bets", "settled");
        checkResponse("/bets/2d", "settled");
        checkResponse("/markets/" + MARKET_ID + "/bets", "settled");
    }

    @Test
    public void testStory() throws Exception {
        checkResponse("/stories/" + BET_ID, "previousActions");
    }

}
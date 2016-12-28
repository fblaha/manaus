package cz.fb.manaus.rest;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration(classes = BetActionController.class)
public class BetActionControllerTest extends AbstractControllerTest {

    @Test
    public void testActionList() throws Exception {
        createMarketWithSingleAction();
        checkResponse("/actions", "betActionType", "actionDate");
        checkResponse("/markets/" + MARKET_ID + "/actions", "betActionType", "actionDate");
    }

}
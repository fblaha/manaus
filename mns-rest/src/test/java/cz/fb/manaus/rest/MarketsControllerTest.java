package cz.fb.manaus.rest;

import cz.fb.manaus.core.test.CoreTestFactory;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration(classes = MarketController.class)
public class MarketsControllerTest extends AbstractControllerTest {

    @Test
    public void testMarketList() throws Exception {
        createMarketWithSingleAction();
        checkResponse("/markets", CoreTestFactory.EVENT_NAME, CoreTestFactory.DRAW_NAME);
        checkResponse("/markets/" + MARKET_ID, CoreTestFactory.EVENT_NAME, CoreTestFactory.DRAW_NAME);
    }

}
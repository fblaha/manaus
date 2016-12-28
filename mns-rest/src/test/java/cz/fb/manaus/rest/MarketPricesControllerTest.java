package cz.fb.manaus.rest;

import cz.fb.manaus.core.test.CoreTestFactory;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration(classes = MarketPricesController.class)
public class MarketPricesControllerTest extends AbstractControllerTest {

    @Test
    public void testPrices() throws Exception {
        createMarketWithSingleAction();
        checkResponse("/markets/" + MARKET_ID + "/prices", "lastMatchedPrice", "selectionId", "prices");
        checkResponse("/markets/" + MARKET_ID + "/prices/" + CoreTestFactory.DRAW,
                "lastMatchedPrice", "selectionId", "prices");
    }


}
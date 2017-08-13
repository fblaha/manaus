package cz.fb.manaus.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import static cz.fb.manaus.core.test.CoreTestFactory.newMarketPrices;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ContextConfiguration(classes = MarketPricesController.class)
public class MarketPricesControllerTest extends AbstractControllerTest {

    @Test
    public void testPrices() throws Exception {
        createMarketWithSingleAction();
        checkResponse("/markets/" + MARKET_ID + "/prices", "lastMatchedPrice", "selectionId", "prices");
        checkResponse("/markets/" + MARKET_ID + "/prices/" + CoreTestFactory.DRAW,
                "lastMatchedPrice", "selectionId", "prices");
    }

    @Test
    public void testAddPrices() throws Exception {
        createMarketWithSingleAction();
        String prices = new ObjectMapper().writer().writeValueAsString(newMarketPrices(3, 2.8d));
        mvc.perform(post("/markets/{id}/prices", MARKET_ID)
                .content(prices)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andReturn();
    }
}
package cz.fb.manaus.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ContextConfiguration(classes = MarketController.class)
public class MarketControllerTest extends AbstractControllerTest {

    @Test
    public void testMarketList() throws Exception {
        createMarketWithSingleAction();
        checkResponse("/markets", CoreTestFactory.EVENT_NAME, CoreTestFactory.DRAW_NAME);
        checkResponse("/markets/" + MARKET_ID, CoreTestFactory.EVENT_NAME, CoreTestFactory.DRAW_NAME);
    }

    @Test
    public void testMarketCreate() throws Exception {
        String market = new ObjectMapper().writer().writeValueAsString(CoreTestFactory.newMarket());
        MvcResult result = mvc.perform(post("/markets")
                .content(market)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        assertThat(result.getResponse().getHeader(HttpHeaders.LOCATION), notNullValue());
    }
}
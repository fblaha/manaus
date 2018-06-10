package cz.fb.manaus.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.BetActionType;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.Date;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ContextConfiguration(classes = BetActionController.class)
public class BetActionControllerTest extends AbstractControllerTest {

    private SettledBet bet;

    @Before
    public void setUp() {
        bet = createMarketWithSingleSettledBet();
    }

    @Test
    public void testActionList() throws Exception {
        checkResponse("/actions", "betActionType", "actionDate");
        checkResponse("/markets/" + MARKET_ID + "/actions", "betActionType", "actionDate");
    }

    @Test
    public void testPostAction() throws Exception {
        var mapper = new ObjectMapper();
        var original = createBetAction();
        int priceId = marketPricesDao.getPrices(MARKET_ID).get(0).getId();
        var serialized = mapper.writer().writeValueAsString(original);
        var result = mvc.perform(post(
                "/markets/{id}/actions?priceId={priceId}", MARKET_ID, priceId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialized))
                .andExpect(status().isCreated())
                .andReturn();
        assertThat(result.getResponse().getHeader(HttpHeaders.LOCATION), notNullValue());
    }

    @Test
    public void testSetBetId() throws Exception {
        var actionId = bet.getBetAction().getId();
        mvc.perform(put(
                "/actions/{id}/betId", actionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("100"))
                .andExpect(status().isOk())
                .andReturn();
    }

    private BetAction createBetAction() {
        var betAction = BetAction.create(BetActionType.UPDATE, new Date(),
                new Price(2d, 3d, Side.LAY), null, CoreTestFactory.DRAW);
        betAction.setProperties(Collections.singletonMap("key", "val"));
        betAction.setBetId("150");
        return betAction;
    }
}
package cz.fb.manaus.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.BetActionType;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.Date;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ContextConfiguration(classes = BetActionController.class)
public class BetActionControllerTest extends AbstractControllerTest {

    @Before
    public void setUp() throws Exception {
        createMarketWithSingleSettledBet();
    }

    @Test
    public void testActionList() throws Exception {
        checkResponse("/actions", "betActionType", "actionDate");
        checkResponse("/markets/" + MARKET_ID + "/actions", "betActionType", "actionDate");
    }

    @Test
    public void testPostAction() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        BetAction original = createBetAction();
        int priceId = marketPricesDao.getPrices(MARKET_ID).get(0).getId();
        String serialized = mapper.writer().writeValueAsString(original);
        MvcResult result = mvc.perform(post(
                "/markets/{id}/actions?priceId={priceId}", MARKET_ID, priceId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialized))
                .andExpect(status().isCreated())
                .andReturn();
        assertThat(result.getResponse().getHeader(HttpHeaders.LOCATION), notNullValue());
    }

    private BetAction createBetAction() {
        BetAction betAction = new BetAction(BetActionType.UPDATE, new Date(),
                new Price(2d, 3d, Side.LAY), null, CoreTestFactory.DRAW);
        betAction.setProperties(Collections.singletonMap("key", "val"));
        betAction.setBetId("150");
        betAction.setTags(TAGS);
        return betAction;
    }
}
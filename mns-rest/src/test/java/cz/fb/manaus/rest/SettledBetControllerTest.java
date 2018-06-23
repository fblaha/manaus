package cz.fb.manaus.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.SettledBetTest;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ContextConfiguration(classes = SettledBetController.class)
public class SettledBetControllerTest extends AbstractControllerTest {

    @Before
    public void setUp() {
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

    @Test
    public void testPostSettledBet() throws Exception {
        var mapper = new ObjectMapper();
        var original = SettledBetTest.create(CoreTestFactory.DRAW, CoreTestFactory.DRAW_NAME,
                5d, new Date(), new Price(5d, 3d, Side.BACK));
        var serialized = mapper.writer().writeValueAsString(original);
        checkPost(serialized, BET_ID, 202);
        checkPost(serialized, BET_ID + "55", 204);
    }

    private void checkPost(String serialized, String betId, int status) throws Exception {
        mvc.perform(post("/bets?betId={betId}", betId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialized))
                .andExpect(status().is(status))
                .andReturn();
    }
}
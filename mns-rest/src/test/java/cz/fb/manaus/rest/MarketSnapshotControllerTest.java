package cz.fb.manaus.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import static cz.fb.manaus.core.test.CoreTestFactory.newMarketPrices;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ContextConfiguration(classes = MarketSnapshotController.class)
public class MarketSnapshotControllerTest extends AbstractControllerTest {

    @Test
    public void testPushSnapshot() throws Exception {
        createMarketWithSingleAction();
        String prices = new ObjectMapper().writer().writeValueAsString(newMarketPrices(3, 2.8d));
        mvc.perform(post("/markets/{id}/snapshot", MARKET_ID)
                .content(prices)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andReturn();
    }
}
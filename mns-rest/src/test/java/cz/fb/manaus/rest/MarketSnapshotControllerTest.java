package cz.fb.manaus.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fb.manaus.core.model.AccountMoney;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.Date;

import static cz.fb.manaus.core.test.CoreTestFactory.newMarketPrices;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ContextConfiguration(classes = MarketSnapshotController.class)
public class MarketSnapshotControllerTest extends AbstractControllerTest {

    @Test
    public void testPushSnapshot() throws Exception {
        createMarketWithSingleAction();
        MarketPrices marketPrices = newMarketPrices(3, 2.8d);
        MarketSnapshotCrate crate = new MarketSnapshotCrate();
        crate.setPrices(marketPrices);
        AccountMoney accountMoney = new AccountMoney();
        accountMoney.setAvailable(1000);
        accountMoney.setTotal(2000);
        crate.setMoney(accountMoney);
        Bet bet = new Bet("1", marketPrices.getMarket().getId(), CoreTestFactory.DRAW,
                new Price(3d, 5d, Side.BACK), new Date(), 0d);
        crate.setBets(Collections.singletonList(bet));
        String snapshot = new ObjectMapper().writer().writeValueAsString(crate);
        mvc.perform(post("/markets/{id}/snapshot", MARKET_ID)
                .content(snapshot)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }
}
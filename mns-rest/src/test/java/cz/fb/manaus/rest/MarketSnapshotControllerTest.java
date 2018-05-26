package cz.fb.manaus.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fb.manaus.core.model.AccountMoney;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static cz.fb.manaus.core.test.CoreTestFactory.newMarketPrices;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ContextConfiguration(classes = MarketSnapshotController.class)
public class MarketSnapshotControllerTest extends AbstractControllerTest {

    @Test
    public void testPushSnapshot() throws Exception {
        createMarketWithSingleAction();
        var marketPrices = newMarketPrices(3, 2.8d);
        var crate = new MarketSnapshotCrate();
        crate.setPrices(marketPrices);
        var accountMoney = new AccountMoney();
        accountMoney.setAvailable(1000);
        accountMoney.setTotal(2000);
        crate.setMoney(accountMoney);
        crate.setScanTime(1000);
        crate.setCategoryBlacklist(Set.of("bad"));
        var bet = new Bet("1", marketPrices.getMarket().getId(), CoreTestFactory.DRAW,
                new Price(3d, 5d, Side.BACK), new Date(), 0d);
        crate.setBets(List.of(bet));
        var snapshot = new ObjectMapper().writer().writeValueAsString(crate);
        mvc.perform(post("/markets/{id}/snapshot", MARKET_ID)
                .content(snapshot)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }
}
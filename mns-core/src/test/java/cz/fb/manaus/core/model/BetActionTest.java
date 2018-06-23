package cz.fb.manaus.core.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class BetActionTest {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    public static BetAction create(BetActionType betActionType, Date actionDate, Price price,
                                   Market market, long selectionId) {
        var ba = new BetAction();
        ba.setBetActionType(betActionType);
        ba.setActionDate(actionDate);
        ba.setPrice(price);
        ba.setMarket(market);
        ba.setSelectionId(selectionId);
        return ba;
    }

    @Test
    public void testJsonMarshall() throws Exception {
        Price price = new Price(5d, 5d, Side.BACK);
        BetAction action = create(BetActionType.PLACE, new Date(), price, null, 100);
        String json = MAPPER.writeValueAsString(action);
        assertThat(json, containsString("PLACE"));
    }
}
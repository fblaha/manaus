package cz.fb.manaus.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.BetActionType;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.ProfitRecord;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

import static cz.fb.manaus.core.test.CoreTestFactory.newBetAction;
import static cz.fb.manaus.core.test.CoreTestFactory.newMarket;
import static java.util.Collections.singletonList;

public class JsonMarshallerTest extends AbstractLocalTestCase {
    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testBetActionList() throws Exception {
        BetAction action = new BetAction(BetActionType.PLACE, new Date(), new Price(2d, 5d, Side.LAY), null, 10);
        Map<String, String> props = ImmutableMap.of("property1", "value1", "reciprocal", "0.92");
        action.setProperties(props);
        String json = mapper.writer().writeValueAsString(singletonList(action));
        System.out.println("json = " + json);
    }

    @Test
    public void testSettledBetList() throws Exception {
        SettledBet bet = new SettledBet(555, "The Draw", 5.23d, new Date(), new Price(2.02d, 2.35d, Side.LAY));
        bet.setBetAction(newBetAction("1", newMarket()));
        String json = mapper.writer().writeValueAsString(singletonList(bet));
        System.out.println("json = " + json);
    }

    @Test
    public void testProfitRecordList() throws Exception {
        ProfitRecord profitRecord = new ProfitRecord("test_name", 5d, 2, 1, 2d, 0.2);
        String json = mapper.writer().writeValueAsString(singletonList(profitRecord));
        System.out.println("json = " + json);
    }

}

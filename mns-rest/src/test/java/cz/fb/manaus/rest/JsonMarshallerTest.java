package cz.fb.manaus.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fb.manaus.core.model.BetActionTest;
import cz.fb.manaus.core.model.BetActionType;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.ProfitRecord;
import cz.fb.manaus.core.model.SettledBetTest;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static cz.fb.manaus.core.test.CoreTestFactory.newBetAction;
import static cz.fb.manaus.core.test.CoreTestFactory.newMarket;
import static org.junit.Assert.assertThat;

public class JsonMarshallerTest extends AbstractLocalTestCase {
    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testBetActionList() throws Exception {
        var action = BetActionTest.create(BetActionType.PLACE, new Date(), new Price(2d, 5d, Side.LAY), null, 10);
        var props = Map.of("property1", "value1", "reciprocal", "0.92");
        action.setProperties(props);
        var json = mapper.writer().writeValueAsString(List.of(action));
        assertThat(json, CoreMatchers.containsString("value1"));
    }

    @Test
    public void testSettledBetList() throws Exception {
        var bet = SettledBetTest.create(555, "The Draw", 5.23d, new Date(), new Price(2.02d, 2.35d, Side.LAY));
        bet.setBetAction(newBetAction("1", newMarket()));
        var json = mapper.writer().writeValueAsString(List.of(bet));
        assertThat(json, CoreMatchers.containsString("The Draw"));
    }

    @Test
    public void testProfitRecordList() throws Exception {
        var profitRecord = new ProfitRecord("test_name", 5d, 2, 1, 2d, 0.2);
        var json = mapper.writer().writeValueAsString(List.of(profitRecord));
        assertThat(json, CoreMatchers.containsString("test_name"));
    }

}

package cz.fb.manaus.core.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertTrue;

public class CollectedBetsTest {

    @Test
    public void testSerialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Bet original = new Bet("111", "222", 333,
                new Price(3d, 2d, Side.BACK), new Date(), 0d);

        CollectedBets bets = CollectedBets.create();
        bets.getPlace().add(original);
        bets.getUpdate().add(original);
        bets.getCancel().add("100");

        String serialized = mapper.writer().writeValueAsString(bets);
        JsonNode tree = mapper.reader().readTree(serialized);
        assertTrue(tree.has("place"));
        assertTrue(tree.has("update"));
        assertTrue(tree.has("cancel"));
    }
}
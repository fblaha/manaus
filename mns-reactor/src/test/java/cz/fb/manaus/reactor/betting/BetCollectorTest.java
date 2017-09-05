package cz.fb.manaus.reactor.betting;

import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class BetCollectorTest {

    public static final String MARKET_ID = "44";
    public static final int SELECTION_ID = 111;

    private final List<String> tested = new LinkedList<>();

    @Test
    public void testFindBet() throws Exception {
        Bet updateBet = new Bet("777", MARKET_ID, SELECTION_ID, new Price(5d, 5d, Side.LAY), null, 0d);
        BetCollector collector = new BetCollector();
        collector.updateBet(new BetCommand(updateBet, new BetAction()));
        checkCollector(collector);
        collector = new BetCollector();
        Bet placeBet = new Bet(null, MARKET_ID, SELECTION_ID, new Price(5d, 5d, Side.LAY), null, 0d);
        collector.placeBet(new BetCommand(placeBet, new BetAction()));
        checkCollector(collector);
    }

    private void checkCollector(BetCollector collector) {
        assertTrue(collector.findBet(MARKET_ID, SELECTION_ID, Side.LAY).isPresent());
        assertFalse(collector.findBet(MARKET_ID, SELECTION_ID, Side.BACK).isPresent());
        assertFalse(collector.findBet(MARKET_ID + 1, SELECTION_ID, Side.LAY).isPresent());
        assertFalse(collector.findBet(MARKET_ID, SELECTION_ID + 1, Side.LAY).isPresent());
    }

    @Test
    public void testEmpty() throws Exception {
        BetCollector collector = new BetCollector();
        assertTrue(collector.isEmpty());
    }

}

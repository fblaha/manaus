package cz.fb.manaus.reactor.betting;

import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class BetCollectorTest {

    public static final String MARKET_ID = "44";
    public static final int SELECTION_ID = 111;

    private final List<String> tested = new LinkedList<>();

    @Test
    public void testFindBet() throws Exception {
        Bet updateBet = new Bet("777", MARKET_ID, SELECTION_ID, new Price(5d, 5d, Side.LAY), null, 0d);
        BetCollector collector = new BetCollector();
        collector.updateBet(new BetCommand(updateBet, null));
        checkCollector(collector);
        collector = new BetCollector();
        Bet placeBet = new Bet(null, MARKET_ID, SELECTION_ID, new Price(5d, 5d, Side.LAY), null, 0d);
        collector.placeBet(new BetCommand(placeBet, null));
        checkCollector(collector);
    }

    private void checkCollector(BetCollector collector) {
        assertTrue(collector.findBet(MARKET_ID, SELECTION_ID, Side.LAY).isPresent());
        assertFalse(collector.findBet(MARKET_ID, SELECTION_ID, Side.BACK).isPresent());
        assertFalse(collector.findBet(MARKET_ID + 1, SELECTION_ID, Side.LAY).isPresent());
        assertFalse(collector.findBet(MARKET_ID, SELECTION_ID + 1, Side.LAY).isPresent());
    }

    @Test
    public void testCallHandlersUpdate() throws Exception {
        BetCollector collector = new BetCollector();
        Bet bet = new Bet("777", MARKET_ID, SELECTION_ID, new Price(5d, 5d, Side.LAY), null, 0d);

        collector.updateBet(new BetCommand(bet, new CheckBetId("555")));
        collector.updateBet(new BetCommand(bet, new CheckBetId("777")));
        List<String> ids = Arrays.asList("555", "777");
        collector.callUpdateHandlers(ids);
        assertThat(tested, is(ids));
        tested.clear();
    }

    @Test
    public void testEmpty() throws Exception {
        BetCollector collector = new BetCollector();
        assertTrue(collector.isEmpty());
    }

    @Test
    public void testCallHandlersPlace() throws Exception {
        BetCollector collector = new BetCollector();
        Bet bet = new Bet(null, MARKET_ID, SELECTION_ID, new Price(5d, 5d, Side.LAY), null, 0d);

        collector.placeBet(new BetCommand(bet, new CheckBetId("222")));
        collector.placeBet(new BetCommand(bet, new CheckBetId("333")));
        collector.placeBet(new BetCommand(bet, new CheckBetId("777")));
        List<String> ids = Arrays.asList("222", "333", "777");
        collector.callPlaceHandlers(ids);
        assertThat(tested, is(ids));

    }

    private class CheckBetId implements Consumer<String> {

        private final String value;

        private CheckBetId(String value) {
            this.value = value;
        }

        @Override
        public void accept(String betId) {
            tested.add(betId);
            assertThat(betId, is(value));
        }
    }
}

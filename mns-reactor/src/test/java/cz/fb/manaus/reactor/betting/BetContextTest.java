package cz.fb.manaus.reactor.betting;

import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.reactor.ReactorTestFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class BetContextTest extends AbstractLocalTestCase {

    @Autowired
    private ReactorTestFactory testFactory;

    @Test
    public void testHalfMatched() throws Exception {
        assertTrue(testFactory.createContext(Side.BACK, 3.5d, 4.6d).isCounterHalfMatched());
        assertTrue(testFactory.createContext(Side.LAY, 3.5d, 4.6d).isCounterHalfMatched());
    }

    @Test
    public void testSimulate() throws Exception {
        BetContext context = testFactory.createContext(Side.LAY, 3.5d, 4.6d);
        SettledBet settledBet = context.withNewPrice(new Price(3d, 5d, Side.LAY)).simulateSettledBet();
        assertThat(settledBet.getPrice().getSide(), is(context.getSide()));
        assertEquals(5d, settledBet.getPrice().getAmount(), 0.0001d);
        assertThat(settledBet.getBetAction(), notNullValue());
    }
}
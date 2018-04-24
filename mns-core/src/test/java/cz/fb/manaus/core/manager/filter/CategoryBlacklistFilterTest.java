package cz.fb.manaus.core.manager.filter;

import cz.fb.manaus.core.model.EventType;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CategoryBlacklistFilterTest extends AbstractLocalTestCase {
    @Autowired
    private CategoryBlacklistFilter filter;
    private Market market;
    private EventType eventType;

    @Before
    public void setUp() throws Exception {
        market = CoreTestFactory.newMarket();
        eventType = new EventType("1", "Soccer");
        market.setEventType(eventType);
    }

    @Test
    public void testFilter() throws Exception {
        assertTrue(filter.accept(market, Set.of()));
        assertFalse(filter.accept(market, Set.of("market_sport_soccer")));
    }
}



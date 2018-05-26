package cz.fb.manaus.core.manager.filter;

import cz.fb.manaus.core.model.Event;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Set;

import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LookAheadFilterTest extends AbstractLocalTestCase {
    @Autowired
    private LookAheadFilter lookAheadFilter;

    @Test
    public void testAccept() {
        var currDate = new Date();
        var market = mock(Market.class);
        var event = mock(Event.class);
        when(market.getEvent()).thenReturn(event);
        when(event.getOpenDate()).thenReturn(addDays(currDate, 50), addDays(currDate, 2));
        assertThat(lookAheadFilter.accept(market, Set.of()), is(false));
        assertThat(lookAheadFilter.accept(market, Set.of()), is(true));
    }
}

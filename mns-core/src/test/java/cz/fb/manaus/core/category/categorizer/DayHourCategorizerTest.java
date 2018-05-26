package cz.fb.manaus.core.category.categorizer;

import cz.fb.manaus.core.model.Event;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;

import static org.apache.commons.lang3.time.DateUtils.addHours;
import static org.apache.commons.lang3.time.DateUtils.addMinutes;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DayHourCategorizerTest extends AbstractLocalTestCase {

    @Autowired
    private DayHourCategorizer categorizer;

    @Test
    public void testGetCategories() {
        var dayStart = DateUtils.truncate(new Date(), Calendar.MONTH);
        var market = mock(Market.class);
        var event = mock(Event.class);
        when(market.getEvent()).thenReturn(event);
        when(event.getOpenDate()).thenReturn(dayStart);
        assertThat(categorizer.getCategories(market).iterator().next(), containsString("0_4"));

        when(event.getOpenDate()).thenReturn(addMinutes(dayStart, 3 * 60 + 59));
        assertThat(categorizer.getCategories(market).iterator().next(), containsString("0_4"));

        when(event.getOpenDate()).thenReturn(addHours(dayStart, 4));
        assertThat(categorizer.getCategories(market).iterator().next(), containsString("4_8"));

        when(event.getOpenDate()).thenReturn(addHours(dayStart, 23));
        assertThat(categorizer.getCategories(market).iterator().next(), containsString("20_24"));

    }
}

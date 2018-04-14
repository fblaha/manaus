package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static java.util.Collections.singleton;
import static org.apache.commons.lang3.time.DateUtils.addHours;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DowngradeCategorizerTest extends AbstractLocalTestCase {
    @Autowired
    private DowngradeCategorizer categorizer;


    @Test
    public void testCategory() throws Exception {
        BetAction place = mock(BetAction.class);
        when(place.getPrice()).thenReturn(new Price(2d, 5d, Side.LAY));
        Date curr = new Date();
        when(place.getActionDate()).thenReturn(addHours(curr, -5));
        BetAction update = mock(BetAction.class);
        when(update.getPrice()).thenReturn(new Price(2.1d, 5d, Side.LAY));
        when(update.getActionDate()).thenReturn(addHours(curr, -2));

        assertThat(categorizer.getCategories(Arrays.asList(place, update), null), is(Collections.<String>emptySet()));


        when(update.getPrice()).thenReturn(new Price(1.9d, 5d, Side.LAY));
        assertThat(categorizer.getCategories(Arrays.asList(place, update), null), hasItems(DowngradeCategorizer.DOWNGRADE, DowngradeCategorizer.DOWNGRADE_LAST));


        BetAction update2 = mock(BetAction.class);
        when(update2.getPrice()).thenReturn(new Price(2.1d, 5d, Side.LAY));
        when(update2.getActionDate()).thenReturn(addHours(curr, -1));
        assertThat(categorizer.getCategories(Arrays.asList(place, update, update2), null), is(singleton(DowngradeCategorizer.DOWNGRADE)));


        try {
            categorizer.getCategories(Arrays.asList(update, place), null);
            fail();
        } catch (IllegalStateException e) {
        }
        try {
            categorizer.getCategories(Arrays.asList(update, update), null);
            fail();
        } catch (IllegalStateException e) {
        }
    }
}

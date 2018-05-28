package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.time.DateUtils.addHours;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DowngradeCategorizerTest extends AbstractLocalTestCase {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Autowired
    private DowngradeCategorizer categorizer;

    @Test
    public void testCategory() {
        var place = mock(BetAction.class);
        when(place.getPrice()).thenReturn(new Price(2d, 5d, Side.LAY));
        var curr = new Date();
        when(place.getActionDate()).thenReturn(addHours(curr, -5));
        var update = mock(BetAction.class);
        when(update.getPrice()).thenReturn(new Price(2.1d, 5d, Side.LAY));
        when(update.getActionDate()).thenReturn(addHours(curr, -2));

        assertThat(categorizer.getCategories(List.of(place, update), null), is(Set.<String>of()));


        when(update.getPrice()).thenReturn(new Price(1.9d, 5d, Side.LAY));
        assertThat(categorizer.getCategories(List.of(place, update), null), hasItems(DowngradeCategorizer.DOWNGRADE, DowngradeCategorizer.DOWNGRADE_LAST));


        var update2 = mock(BetAction.class);
        when(update2.getPrice()).thenReturn(new Price(2.1d, 5d, Side.LAY));
        when(update2.getActionDate()).thenReturn(addHours(curr, -1));
        assertThat(categorizer.getCategories(List.of(place, update, update2), null), is(Set.of(DowngradeCategorizer.DOWNGRADE)));
    }

    @Test
    public void testCategoryMixedSides() {
        var place = mock(BetAction.class);
        when(place.getPrice()).thenReturn(new Price(2d, 5d, Side.LAY));
        var curr = new Date();
        when(place.getActionDate()).thenReturn(addHours(curr, -5));
        var update = mock(BetAction.class);
        when(update.getPrice()).thenReturn(new Price(2.1d, 5d, Side.BACK));
        when(update.getActionDate()).thenReturn(addHours(curr, -2));
        thrown.expectMessage("mixed sides");
        categorizer.getCategories(List.of(place, update), null);
    }

    @Test
    public void testCategoryUnordered() {
        var place = mock(BetAction.class);
        var price = new Price(2d, 5d, Side.LAY);
        when(place.getPrice()).thenReturn(price);
        var curr = new Date();
        when(place.getActionDate()).thenReturn(addHours(curr, -1));
        var update = mock(BetAction.class);
        when(update.getPrice()).thenReturn(price);
        when(update.getActionDate()).thenReturn(addHours(curr, -2));
        thrown.expectMessage("time disorder");
        categorizer.getCategories(List.of(place, update), null);
    }
}

package cz.fb.manaus.core.category.categorizer;

import cz.fb.manaus.core.test.AbstractLocalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class WeekDayCategorizerTest extends AbstractLocalTestCase {

    @Autowired
    private WeekDayCategorizer categorizer;

    @Test
    public void testCategory() {
        var cal = Calendar.getInstance();
        cal.set(2012, Calendar.OCTOBER, 22, 9, 40);
        assertThat(categorizer.getCategory(cal.getTime()), is(Set.of("mon")));
        cal.set(Calendar.DAY_OF_MONTH, 23);
        assertThat(categorizer.getCategory(cal.getTime()), is(Set.of("tue")));
        cal.set(Calendar.DAY_OF_MONTH, 24);
        assertThat(categorizer.getCategory(cal.getTime()), is(Set.of("wed")));
        cal.set(Calendar.DAY_OF_MONTH, 25);
        assertThat(categorizer.getCategory(cal.getTime()), is(Set.of("thu")));
        cal.set(Calendar.DAY_OF_MONTH, 26);
        assertThat(categorizer.getCategory(cal.getTime()), is(Set.of("fri")));
        cal.set(Calendar.DAY_OF_MONTH, 27);
        assertThat(categorizer.getCategory(cal.getTime()), is(Set.of("sat")));
        cal.set(Calendar.DAY_OF_MONTH, 28);
        assertThat(categorizer.getCategory(cal.getTime()), is(Set.of("sun")));
        cal.set(Calendar.DAY_OF_MONTH, 29);
        assertThat(categorizer.getCategory(cal.getTime()), is(Set.of("mon")));
    }
}

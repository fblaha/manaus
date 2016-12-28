package cz.fb.manaus.core.category.categorizer;

import cz.fb.manaus.core.test.AbstractLocalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class WeekDayCategorizerTest extends AbstractLocalTestCase {

    @Autowired
    private WeekDayCategorizer categorizer;

    @Test
    public void testCategory() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.set(2012, Calendar.OCTOBER, 22, 9, 40);
        assertThat(categorizer.getCategory(cal.getTime()), is(Collections.singleton("mon")));
        cal.set(Calendar.DAY_OF_MONTH, 23);
        assertThat(categorizer.getCategory(cal.getTime()), is(Collections.singleton("tue")));
        cal.set(Calendar.DAY_OF_MONTH, 24);
        assertThat(categorizer.getCategory(cal.getTime()), is(Collections.singleton("wed")));
        cal.set(Calendar.DAY_OF_MONTH, 25);
        assertThat(categorizer.getCategory(cal.getTime()), is(Collections.singleton("thu")));
        cal.set(Calendar.DAY_OF_MONTH, 26);
        assertThat(categorizer.getCategory(cal.getTime()), is(Collections.singleton("fri")));
        cal.set(Calendar.DAY_OF_MONTH, 27);
        assertThat(categorizer.getCategory(cal.getTime()), is(Collections.singleton("sat")));
        cal.set(Calendar.DAY_OF_MONTH, 28);
        assertThat(categorizer.getCategory(cal.getTime()), is(Collections.singleton("sun")));
        cal.set(Calendar.DAY_OF_MONTH, 29);
        assertThat(categorizer.getCategory(cal.getTime()), is(Collections.singleton("mon")));
    }


}

package cz.fb.manaus.core.category.categorizer;

import cz.fb.manaus.core.model.Market;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

@Component
final public class WeekDayCategorizer extends AbstractDelegatingCategorizer {

    public static final String PREFIX = "weekDay_";

    public WeekDayCategorizer() {
        super(PREFIX);
    }

    public static String getWeekDay(Date date) {
        return new SimpleDateFormat("E", Locale.US).format(date).toLowerCase();
    }

    @Override
    public Set<String> getCategoryRaw(Market market) {
        Date date = market.getEvent().getOpenDate();
        return getCategory(date);
    }

    Set<String> getCategory(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return Collections.singleton(getWeekDay(date));

    }

}

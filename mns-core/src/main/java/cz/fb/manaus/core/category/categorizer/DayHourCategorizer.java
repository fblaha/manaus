package cz.fb.manaus.core.category.categorizer;

import com.google.common.collect.Range;
import cz.fb.manaus.core.model.Market;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.Range.closedOpen;

@Component
final public class DayHourCategorizer extends AbstractDelegatingCategorizer {


    public static final List<Range<Integer>> RANGES = of(
            closedOpen(0, 4),
            closedOpen(4, 8),
            closedOpen(8, 12),
            closedOpen(12, 16),
            closedOpen(16, 20),
            closedOpen(20, 24));

    public DayHourCategorizer() {
        super("dayHour_");
    }

    @Override
    public Set<String> getCategoryRaw(Market market) {
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(market.getEvent().getOpenDate());
        int hour = startTime.get(Calendar.HOUR_OF_DAY);
        for (Range<Integer> range : RANGES) {
            if (range.contains(hour)) {
                return Set.of(range.lowerEndpoint() + "_" + range.upperEndpoint());
            }
        }
        throw new IllegalStateException();

    }
}

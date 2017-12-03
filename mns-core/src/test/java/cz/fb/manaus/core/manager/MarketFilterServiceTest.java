package cz.fb.manaus.core.manager;

import com.google.common.collect.Range;
import cz.fb.manaus.core.service.AbstractMarketDataAwareTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.TestCase.assertTrue;

public class MarketFilterServiceTest extends AbstractMarketDataAwareTestCase {
    @Autowired
    private MarketFilterService filterService;

    @Test
    public void testFilterService() throws Exception {
        checkFilterCount(Range.closed(1000L, 1500L), false);
        checkFilterCount(Range.singleton(6700L), true);
    }

    public void checkFilterCount(Range<Long> expectedRange, boolean hasBets) {
        System.out.println(" = " + markets.size());
        long cnt = markets.stream()
                .filter(market -> filterService.accept(market, hasBets)).count();
        assertTrue(expectedRange.contains(cnt));
    }
}

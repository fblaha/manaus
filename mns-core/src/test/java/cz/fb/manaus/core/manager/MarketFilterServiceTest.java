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
        long cnt = markets.stream().filter(filterService::accept).count();
        assertTrue(Range.closed(1000L, 1500L).contains(cnt));
    }

}

package cz.fb.manaus.core.manager;

import com.google.common.collect.Range;
import cz.fb.manaus.core.service.AbstractMarketDataAwareTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.google.common.collect.FluentIterable.from;
import static junit.framework.TestCase.assertTrue;

public class MarketFilterServiceTest extends AbstractMarketDataAwareTestCase {
    @Autowired
    private MarketFilterService filterService;

    @Test
    public void testFilterService() throws Exception {
        long cnt = from(markets).filter(filterService::accept).size();
        assertTrue(Range.closed(1000l, 1500l).contains(cnt));
    }


}

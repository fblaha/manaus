package cz.fb.manaus.ischia;

import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.betting.AbstractBettorTest;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.OptionalDouble;

import static java.util.OptionalDouble.of;

@ActiveProfiles("ischia")
public class LayLoserBettorTest extends AbstractBettorTest<LayLoserBettor> {

    @Test
    public void testPlaceFairness() throws Exception {
        checkPlace(persistMarket(reactorTestFactory.createMarket(2.98d, 3.2d, of(3.05d), 1)),
                3, OptionalDouble.of(2.88));
    }

    @Test
    public void testPlaceBest() throws Exception {
        checkPlace(persistMarket(reactorTestFactory.createMarket(2.58d, 3.15d, of(3d), 1)),
                3, OptionalDouble.of(2.6d));
    }

    @Test
    public void testLastMatchedOrTradedVolume() throws Exception {
        checkPlace(persistMarket(reactorTestFactory.createMarket(2.8d, 3.2d, of(2.5), 1)),
                3, OptionalDouble.of(2.48));
    }

    @Test
    public void testCloseUpdate() {
        MarketPrices market = persistMarket(reactorTestFactory.createMarket(2.90d, 3.2d, of(3d), 1));

        checkUpdate(market, 2.86, Side.LAY, 0, 0);
        checkUpdate(market, 2.88, Side.LAY, 0, 0);

        checkUpdate(market, 2.9, Side.LAY, 0, 3);
        checkUpdate(market, 2.84, Side.LAY, 0, 3);
        checkUpdate(market, 2.92, Side.LAY, 0, 3);
        checkUpdate(market, 2.94, Side.LAY, 0, 3);
    }

}

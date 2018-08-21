package cz.fb.manaus.ischia;

import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.betting.AbstractBettorTest;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.OptionalDouble;

import static java.util.OptionalDouble.of;

@ActiveProfiles("ischia")
public class BackLoserBettorTest extends AbstractBettorTest<BackLoserBettor> {

    @Test
    public void testPlaceBest() throws Exception {
        checkPlace(persistMarket(reactorTestFactory.createMarket(2.98d, 3.8d, of(3d), 1)),
                3, OptionalDouble.of(3.75d));
    }

    @Test
    public void testPlaceFairness() throws Exception {
        checkPlace(persistMarket(reactorTestFactory.createMarket(2.98d, 3.3d, of(3d), 1)),
                3, OptionalDouble.of(3.25d));
    }

    @Test
    public void testLastMatchedOrTradedVolume() throws Exception {
        checkPlace(persistMarket(reactorTestFactory.createMarket(2.98d, 3.2d, of(4d), 1)),
                3, OptionalDouble.of(4.0));
    }

    @Test
    public void testCloseUpdate() {
        var market = persistMarket(reactorTestFactory.createMarket(2.8d, 3.4d, of(3d), 1));
        checkUpdate(market, 3.4d, Side.BACK, 0, 0);
        checkUpdate(market, 3.35d, Side.BACK, 0, 0);
        checkUpdate(market, 3.5d, Side.BACK, 0, 3);
        checkUpdate(market, 3.3d, Side.BACK, 0, 3);
        checkUpdate(market, 3.25d, Side.BACK, 0, 3);
    }

}

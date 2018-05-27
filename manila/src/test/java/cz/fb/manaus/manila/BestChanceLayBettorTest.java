package cz.fb.manaus.manila;

import cz.fb.manaus.reactor.betting.AbstractBettorTest;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.OptionalDouble;


@ActiveProfiles("manila")
public class BestChanceLayBettorTest extends AbstractBettorTest<BestChanceLayBettor> {

    @Test
    public void testPlacePositive() throws Exception {
        checkPlace(persistMarket(reactorTestFactory.createMarket(0.2, List.of(0.5, 0.3, 0.2))),
                1, OptionalDouble.of(1.81d));
        checkPlace(persistMarket(reactorTestFactory.createMarket(0.25, List.of(0.7, 0.2, 0.1))),
                1, OptionalDouble.of(1.33d));
    }

    @Test
    public void testPlaceLowPrice() throws Exception {
        checkPlace(persistMarket(reactorTestFactory.createMarket(0.3, List.of(0.9, 0.05, 0.05))),
                0, OptionalDouble.empty());
        checkPlace(persistMarket(reactorTestFactory.createMarket(0.3, List.of(0.7, 0.2, 0.1))),
                1, OptionalDouble.of(1.31d));
    }

}
